/*_##########################################################################
  _##
  _##  Copyright (C) 2011  Kaito Yamada
  _##
  _##########################################################################
*/

package com.github.network;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.pcap4j.packet.Packet;
import org.snmp4j.log.LogAdapter;
import org.snmp4j.log.LogFactory;
import com.github.util.NamedThreadFactory;

public abstract class PacketReceiver {

  protected static final LogAdapter logger
    = LogFactory.getLogger(PacketReceiver.class);

  private static final long AWAIT_TERMINATION_TIMEOUT = 2000;
  private static final TimeUnit AWAIT_TERMINATION_TIMEOUT_UNIT
    = TimeUnit.MILLISECONDS;

  private final String name;
  private final StoppableLinkedBlockingQueue<Packet> recvPacketQueue
    = new StoppableLinkedBlockingQueue<Packet>(
        NetworkPropertiesLoader.getPacketQueueSize()
      );
  private final ExecutorService packetTakerExecutor;
  private final ExecutorService packetProcessorThreadPool;
  private final Object thisLock = new Object();

  private Future<?> packetTakerFuture;
  private volatile boolean running = false;

  public PacketReceiver(String name) {
    this.name = name;
    this.packetTakerExecutor
      = Executors.newSingleThreadExecutor(
          new NamedThreadFactory(
              name + "_" + PacketTaker.class.getSimpleName(),
            true
          )
        );
    this.packetProcessorThreadPool
      = Executors.newCachedThreadPool(
          new NamedThreadFactory(name + "_packetProcessor", true)
        );
  }

  public String getName() {
    return name;
  }

  public BlockingQueue<Packet> getRecvPacketQueue() {
    return recvPacketQueue;
  }

  public void start() {
    synchronized (thisLock) {
      if (isRunning()) {
        logger.warn("Already started");
        return;
      }

      recvPacketQueue.start();
      packetTakerFuture = packetTakerExecutor.submit(new PacketTaker());
      running = true;
    }
  }

  public void stop() {
    synchronized (thisLock) {
      if (!isRunning()) {
        logger.warn("Already stopped");
        return;
      }

      packetTakerFuture.cancel(true);

      recvPacketQueue.stop();
      running = false;
    }
  }

  public void shutdown() {
    synchronized (thisLock) {
      if (running) {
        stop();
      }

      packetTakerExecutor.shutdown();
      packetProcessorThreadPool.shutdown();
      try {
        boolean terminated
          = packetTakerExecutor.awaitTermination(
              AWAIT_TERMINATION_TIMEOUT,
              AWAIT_TERMINATION_TIMEOUT_UNIT
            );
        if (!terminated) {
          logger.warn("Couldn't terminate packetTakerExecutor.");
        }

        terminated
          = packetProcessorThreadPool.awaitTermination(
              AWAIT_TERMINATION_TIMEOUT,
              AWAIT_TERMINATION_TIMEOUT_UNIT
            );
        if (!terminated) {
          logger.warn("Couldn't terminate packetProcessorThreadPool.");
        }
      } catch (InterruptedException e) {
        logger.warn(e);
      }
    }

    logger.info("shutdowned");
  }

  public boolean isRunning() {
    return running;
  }

  protected abstract void process(Packet packet);

  private class PacketTaker implements Runnable {

    public void run() {
      logger.info("start.");
      while (isRunning()) {
        try {
          final Packet packet = recvPacketQueue.take();
          packetProcessorThreadPool.execute(
            new Runnable() {
              public void run() {
                process(packet);
              }
            }
          );
        } catch (InterruptedException e) {
          break;
        }
      }
      logger.info("stopped.");
    }

  }

}
