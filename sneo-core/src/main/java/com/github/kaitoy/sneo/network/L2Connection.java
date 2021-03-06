/*_##########################################################################
  _##
  _##  Copyright (C) 2011  Kaito Yamada
  _##
  _##########################################################################
*/

package com.github.kaitoy.sneo.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.pcap4j.packet.Packet;

public class L2Connection extends PacketReceiver {

  private final List<PhysicalNetworkInterface> connectedNifs
    = Collections.synchronizedList(new ArrayList<PhysicalNetworkInterface>());

  private L2Connection(String name) { super(name); }

  public static L2Connection connect(
    PhysicalNetworkInterface nif1, PhysicalNetworkInterface nif2
  ) {
    L2Connection l2
      = new L2Connection(
          L2Connection.class.getSimpleName()
            + "_" + nif1.getName() + "_" + nif2.getName()
        );

    l2.connectedNifs.add(nif1);
    nif1.setSendPacketQueue(l2.getRecvPacketQueue());

    l2.connectedNifs.add(nif2);
    nif2.setSendPacketQueue(l2.getRecvPacketQueue());

    return l2;
  }

  public static L2Connection connect(
    String name, PhysicalNetworkInterface... nifs
  ) {
    L2Connection l2 = new L2Connection(name);

    for (PhysicalNetworkInterface nif: nifs) {
      l2.connectedNifs.add(nif);
      nif.setSendPacketQueue(l2.getRecvPacketQueue());
    }

    return l2;
  }

  public void addConnection(PhysicalNetworkInterface nif) {
    connectedNifs.add(nif);
    nif.setSendPacketQueue(getRecvPacketQueue());
  }

  @Override
  public void process(PacketContainer pc) {
    Packet packet = pc.getPacket();

    if (logger.isDebugEnabled()) {
      StringBuilder sb = new StringBuilder();
      sb.append("Received a packet from ")
        .append(pc.getSrc().getName())
        .append(": ")
        .append(packet);
      logger.debug(sb.toString());
    }

    NetworkInterface src = pc.getSrc();

    for (PhysicalNetworkInterface nif: connectedNifs) {
      if (src != nif && nif.isRunning()) {
        boolean offered = nif.getRecvPacketQueue().offer(new PacketContainer(packet, null));
        if (offered) {
          if (logger.isDebugEnabled()) {
            logger.debug("Sent a packet: " + packet);
          }
        }
        else {
          logger.error("Couldn't send a packet: " + packet);
        }
      }
    }
  }

}
