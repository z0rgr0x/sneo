/*_##########################################################################
  _##
  _##  Copyright (C) 2012 Kaito Yamada
  _##
  _##########################################################################
*/

package com.github.kaitoy.sneo.giane.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import com.github.kaitoy.sneo.network.dto.VlanDto;
import com.github.kaitoy.sneo.network.dto.VlanMemberDto;
import com.opensymphony.xwork2.validator.annotations.IntRangeFieldValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredFieldValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import com.opensymphony.xwork2.validator.annotations.StringLengthFieldValidator;

@Entity
@Table(name = "VLAN")
public class Vlan implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = 5819690592116317232L;

  private Integer id;
  private String name;
  private Integer vid;
  private VlanIpAddressRelation ipAddressRelation;
  private List<VlanMember> vlanMembers;
  private Node node;

  @Id
  @GeneratedValue(generator = "SequenceStyleGenerator")
  @GenericGenerator(
    name = "SequenceStyleGenerator",
    strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
    parameters = {
      @Parameter(name = "sequence_name", value = "VLAN_SEQUENCE"),
      @Parameter(name = "initial_value", value = "1"),
      @Parameter(name = "increment_size", value = "1")
    }
  )
  @Column(name = "ID")
  public Integer getId() { return id; }

  public void setId(Integer id) { this.id = id; }

  @Column(name = "NAME", nullable = false, length = 50)
  public String getName() {
    return name;
  }

  @RequiredStringValidator(
    key = "RequiredStringValidator.error",
    trim = true,
    shortCircuit = true // Stops checking if detects error
  )
  @StringLengthFieldValidator(
    key = "StringLengthFieldValidator.error.max",
    trim = true,
    maxLength = "50",
    shortCircuit = true
  )
  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "VID", nullable = false)
  public Integer getVid() {
    return vid;
  }

  @RequiredFieldValidator(
    key = "RequiredFieldValidator.error",
    shortCircuit = true
  )
  @IntRangeFieldValidator(
    key = "IntRangeFieldValidator.error.min.max",
    min = "1",
    max = "4094",
    shortCircuit = true
  )
  public void setVid(Integer vid) {
    this.vid = vid;
  }

  @OneToOne(
    fetch = FetchType.LAZY,
    orphanRemoval = true,
    cascade = {
      CascadeType.REMOVE
    },
    optional = false
  )
  @JoinColumn(
    name = "IP_ADDRESS_RELATION_ID",
    nullable = false,
    unique = true
  )
  public VlanIpAddressRelation getIpAddressRelation() {
    return ipAddressRelation;
  }

  public void setIpAddressRelation(VlanIpAddressRelation ipAddressRelation) {
    this.ipAddressRelation = ipAddressRelation;
  }

  @ManyToMany(fetch = FetchType.LAZY, cascade = {})
  @JoinTable(
    name = "VLAN__VLAN_MEMBER",
    joinColumns = @JoinColumn(name = "VLAN_ID", nullable = false),
    inverseJoinColumns = @JoinColumn(name = "VLAN_MEMBER_ID", nullable = false)
  )
  public List<VlanMember> getVlanMembers() {
    return vlanMembers;
  }

  public void setVlanMembers(List<VlanMember> vlanMembers) {
    this.vlanMembers = vlanMembers;
  }

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "NODE_ID", nullable = false)
  public Node getNode() {
    return node;
  }

  public void setNode(Node node) {
    this.node = node;
  }

  public VlanDto toDto() {
    List<VlanMemberDto> vlanMemberDtos
      = new ArrayList<VlanMemberDto>();
    for (VlanMember vlanMember: vlanMembers) {
      vlanMemberDtos.add(vlanMember.toDto());
    }

    VlanDto dto = new VlanDto();
    dto.setId(id);
    dto.setName(name);
    dto.setVid(vid);
    dto.setVlanMembers(vlanMemberDtos);
    dto.setIpAddresses(ipAddressRelation.getIpAddressDtos());
    return dto;
  }

}
