package org.whispersystems.signalservice.api.storage;

import com.google.protobuf.ByteString;

import org.signal.zkgroup.InvalidInputException;
import org.signal.zkgroup.groups.GroupMasterKey;
import org.whispersystems.signalservice.api.util.ProtoUtil;
import org.whispersystems.signalservice.internal.storage.protos.GroupV2Record;

import java.util.Objects;

public final class SignalGroupV2Record implements SignalRecord {

  private final StorageId      id;
  private final GroupV2Record  proto;
  private final GroupMasterKey masterKey;
  private final boolean        hasUnknownFields;

  public SignalGroupV2Record(StorageId id, GroupV2Record proto) {
    this.id               = id;
    this.proto            = proto;
    this.hasUnknownFields = ProtoUtil.hasUnknownFields(proto);
    try {
      this.masterKey = new GroupMasterKey(proto.getMasterKey().toByteArray());
    } catch (InvalidInputException e) {
      throw new AssertionError(e);
    }
  }

  @Override
  public StorageId getId() {
    return id;
  }

  public boolean hasUnknownFields() {
    return hasUnknownFields;
  }

  public byte[] serializeUnknownFields() {
    return hasUnknownFields ? proto.toByteArray() : null;
  }

  public GroupMasterKey getMasterKey() {
    return masterKey;
  }

  public boolean isBlocked() {
    return proto.getBlocked();
  }

  public boolean isProfileSharingEnabled() {
    return proto.getWhitelisted();
  }

  public boolean isArchived() {
    return proto.getArchived();
  }

  GroupV2Record toProto() {
    return proto;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SignalGroupV2Record that = (SignalGroupV2Record) o;
    return id.equals(that.id) &&
        proto.equals(that.proto);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, proto);
  }

  public static final class Builder {
    private final StorageId             id;
    private final GroupV2Record.Builder builder;

    private byte[] unknownFields;

    public Builder(byte[] rawId, GroupMasterKey masterKey) {
      this.id      = StorageId.forGroupV2(rawId);
      this.builder = GroupV2Record.newBuilder();

      builder.setMasterKey(ByteString.copyFrom(masterKey.serialize()));
    }

    public Builder setUnknownFields(byte[] serializedUnknowns) {
      this.unknownFields = serializedUnknowns;
      return this;
    }

    public Builder setBlocked(boolean blocked) {
      builder.setBlocked(blocked);
      return this;
    }

    public Builder setProfileSharingEnabled(boolean profileSharingEnabled) {
      builder.setWhitelisted(profileSharingEnabled);
      return this;
    }

    public Builder setArchived(boolean archived) {
      builder.setArchived(archived);
      return this;
    }

    public SignalGroupV2Record build() {
      GroupV2Record proto = builder.build();

      if (unknownFields != null) {
        proto = ProtoUtil.combineWithUnknownFields(proto, unknownFields);
      }

      return new SignalGroupV2Record(id, proto);
    }
  }
}
