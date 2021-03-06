// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: program.proto

package com.sdmc.dtv.programsend;

public final class ProgramToPhone {
  private ProgramToPhone() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public interface ProgramOrBuilder
      extends com.google.protobuf.MessageOrBuilder {
    
    // required int32 id = 1;
    boolean hasId();
    int getId();
    
    // required string number = 2;
    boolean hasNumber();
    String getNumber();
    
    // required string name = 3;
    boolean hasName();
    String getName();
    
    // required int32 type = 4;
    boolean hasType();
    int getType();
    
    // required bool isScrambler = 5;
    boolean hasIsScrambler();
    boolean getIsScrambler();
    
    // required bool isFavor = 6;
    boolean hasIsFavor();
    boolean getIsFavor();
    
    // required bool isLock = 7;
    boolean hasIsLock();
    boolean getIsLock();
  }
  public static final class Program extends
      com.google.protobuf.GeneratedMessage
      implements ProgramOrBuilder {
    // Use Program.newBuilder() to construct.
    private Program(Builder builder) {
      super(builder);
    }
    private Program(boolean noInit) {}
    
    private static final Program defaultInstance;
    public static Program getDefaultInstance() {
      return defaultInstance;
    }
    
    public Program getDefaultInstanceForType() {
      return defaultInstance;
    }
    
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.sdmc.dtv.programsend.ProgramToPhone.internal_static_com_sdmc_dtv_programsend_Program_descriptor;
    }
    
    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.sdmc.dtv.programsend.ProgramToPhone.internal_static_com_sdmc_dtv_programsend_Program_fieldAccessorTable;
    }
    
    private int bitField0_;
    // required int32 id = 1;
    public static final int ID_FIELD_NUMBER = 1;
    private int id_;
    public boolean hasId() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    public int getId() {
      return id_;
    }
    
    // required string number = 2;
    public static final int NUMBER_FIELD_NUMBER = 2;
    private java.lang.Object number_;
    public boolean hasNumber() {
      return ((bitField0_ & 0x00000002) == 0x00000002);
    }
    public String getNumber() {
      java.lang.Object ref = number_;
      if (ref instanceof String) {
        return (String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        if (com.google.protobuf.Internal.isValidUtf8(bs)) {
          number_ = s;
        }
        return s;
      }
    }
    private com.google.protobuf.ByteString getNumberBytes() {
      java.lang.Object ref = number_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8((String) ref);
        number_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    
    // required string name = 3;
    public static final int NAME_FIELD_NUMBER = 3;
    private java.lang.Object name_;
    public boolean hasName() {
      return ((bitField0_ & 0x00000004) == 0x00000004);
    }
    public String getName() {
      java.lang.Object ref = name_;
      if (ref instanceof String) {
        return (String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        if (com.google.protobuf.Internal.isValidUtf8(bs)) {
          name_ = s;
        }
        return s;
      }
    }
    private com.google.protobuf.ByteString getNameBytes() {
      java.lang.Object ref = name_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8((String) ref);
        name_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    
    // required int32 type = 4;
    public static final int TYPE_FIELD_NUMBER = 4;
    private int type_;
    public boolean hasType() {
      return ((bitField0_ & 0x00000008) == 0x00000008);
    }
    public int getType() {
      return type_;
    }
    
    // required bool isScrambler = 5;
    public static final int ISSCRAMBLER_FIELD_NUMBER = 5;
    private boolean isScrambler_;
    public boolean hasIsScrambler() {
      return ((bitField0_ & 0x00000010) == 0x00000010);
    }
    public boolean getIsScrambler() {
      return isScrambler_;
    }
    
    // required bool isFavor = 6;
    public static final int ISFAVOR_FIELD_NUMBER = 6;
    private boolean isFavor_;
    public boolean hasIsFavor() {
      return ((bitField0_ & 0x00000020) == 0x00000020);
    }
    public boolean getIsFavor() {
      return isFavor_;
    }
    
    // required bool isLock = 7;
    public static final int ISLOCK_FIELD_NUMBER = 7;
    private boolean isLock_;
    public boolean hasIsLock() {
      return ((bitField0_ & 0x00000040) == 0x00000040);
    }
    public boolean getIsLock() {
      return isLock_;
    }
    
    private void initFields() {
      id_ = 0;
      number_ = "";
      name_ = "";
      type_ = 0;
      isScrambler_ = false;
      isFavor_ = false;
      isLock_ = false;
    }
    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized != -1) return isInitialized == 1;
      
      if (!hasId()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasNumber()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasName()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasType()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasIsScrambler()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasIsFavor()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasIsLock()) {
        memoizedIsInitialized = 0;
        return false;
      }
      memoizedIsInitialized = 1;
      return true;
    }
    
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        output.writeInt32(1, id_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        output.writeBytes(2, getNumberBytes());
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        output.writeBytes(3, getNameBytes());
      }
      if (((bitField0_ & 0x00000008) == 0x00000008)) {
        output.writeInt32(4, type_);
      }
      if (((bitField0_ & 0x00000010) == 0x00000010)) {
        output.writeBool(5, isScrambler_);
      }
      if (((bitField0_ & 0x00000020) == 0x00000020)) {
        output.writeBool(6, isFavor_);
      }
      if (((bitField0_ & 0x00000040) == 0x00000040)) {
        output.writeBool(7, isLock_);
      }
      getUnknownFields().writeTo(output);
    }
    
    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;
    
      size = 0;
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(1, id_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(2, getNumberBytes());
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(3, getNameBytes());
      }
      if (((bitField0_ & 0x00000008) == 0x00000008)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(4, type_);
      }
      if (((bitField0_ & 0x00000010) == 0x00000010)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBoolSize(5, isScrambler_);
      }
      if (((bitField0_ & 0x00000020) == 0x00000020)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBoolSize(6, isFavor_);
      }
      if (((bitField0_ & 0x00000040) == 0x00000040)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBoolSize(7, isLock_);
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }
    
    private static final long serialVersionUID = 0L;
    @java.lang.Override
    protected java.lang.Object writeReplace()
        throws java.io.ObjectStreamException {
      return super.writeReplace();
    }
    
    public static com.sdmc.dtv.programsend.ProgramToPhone.Program parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static com.sdmc.dtv.programsend.ProgramToPhone.Program parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static com.sdmc.dtv.programsend.ProgramToPhone.Program parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static com.sdmc.dtv.programsend.ProgramToPhone.Program parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static com.sdmc.dtv.programsend.ProgramToPhone.Program parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static com.sdmc.dtv.programsend.ProgramToPhone.Program parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    public static com.sdmc.dtv.programsend.ProgramToPhone.Program parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static com.sdmc.dtv.programsend.ProgramToPhone.Program parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input, extensionRegistry)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static com.sdmc.dtv.programsend.ProgramToPhone.Program parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static com.sdmc.dtv.programsend.ProgramToPhone.Program parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    
    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(com.sdmc.dtv.programsend.ProgramToPhone.Program prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }
    
    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessage.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder>
       implements com.sdmc.dtv.programsend.ProgramToPhone.ProgramOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return com.sdmc.dtv.programsend.ProgramToPhone.internal_static_com_sdmc_dtv_programsend_Program_descriptor;
      }
      
      protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return com.sdmc.dtv.programsend.ProgramToPhone.internal_static_com_sdmc_dtv_programsend_Program_fieldAccessorTable;
      }
      
      // Construct using com.sdmc.dtv.programsend.ProgramToPhone.Program.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }
      
      private Builder(BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders) {
        }
      }
      private static Builder create() {
        return new Builder();
      }
      
      public Builder clear() {
        super.clear();
        id_ = 0;
        bitField0_ = (bitField0_ & ~0x00000001);
        number_ = "";
        bitField0_ = (bitField0_ & ~0x00000002);
        name_ = "";
        bitField0_ = (bitField0_ & ~0x00000004);
        type_ = 0;
        bitField0_ = (bitField0_ & ~0x00000008);
        isScrambler_ = false;
        bitField0_ = (bitField0_ & ~0x00000010);
        isFavor_ = false;
        bitField0_ = (bitField0_ & ~0x00000020);
        isLock_ = false;
        bitField0_ = (bitField0_ & ~0x00000040);
        return this;
      }
      
      public Builder clone() {
        return create().mergeFrom(buildPartial());
      }
      
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.sdmc.dtv.programsend.ProgramToPhone.Program.getDescriptor();
      }
      
      public com.sdmc.dtv.programsend.ProgramToPhone.Program getDefaultInstanceForType() {
        return com.sdmc.dtv.programsend.ProgramToPhone.Program.getDefaultInstance();
      }
      
      public com.sdmc.dtv.programsend.ProgramToPhone.Program build() {
        com.sdmc.dtv.programsend.ProgramToPhone.Program result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }
      
      private com.sdmc.dtv.programsend.ProgramToPhone.Program buildParsed()
          throws com.google.protobuf.InvalidProtocolBufferException {
        com.sdmc.dtv.programsend.ProgramToPhone.Program result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(
            result).asInvalidProtocolBufferException();
        }
        return result;
      }
      
      public com.sdmc.dtv.programsend.ProgramToPhone.Program buildPartial() {
        com.sdmc.dtv.programsend.ProgramToPhone.Program result = new com.sdmc.dtv.programsend.ProgramToPhone.Program(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        result.id_ = id_;
        if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
          to_bitField0_ |= 0x00000002;
        }
        result.number_ = number_;
        if (((from_bitField0_ & 0x00000004) == 0x00000004)) {
          to_bitField0_ |= 0x00000004;
        }
        result.name_ = name_;
        if (((from_bitField0_ & 0x00000008) == 0x00000008)) {
          to_bitField0_ |= 0x00000008;
        }
        result.type_ = type_;
        if (((from_bitField0_ & 0x00000010) == 0x00000010)) {
          to_bitField0_ |= 0x00000010;
        }
        result.isScrambler_ = isScrambler_;
        if (((from_bitField0_ & 0x00000020) == 0x00000020)) {
          to_bitField0_ |= 0x00000020;
        }
        result.isFavor_ = isFavor_;
        if (((from_bitField0_ & 0x00000040) == 0x00000040)) {
          to_bitField0_ |= 0x00000040;
        }
        result.isLock_ = isLock_;
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }
      
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof com.sdmc.dtv.programsend.ProgramToPhone.Program) {
          return mergeFrom((com.sdmc.dtv.programsend.ProgramToPhone.Program)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }
      
      public Builder mergeFrom(com.sdmc.dtv.programsend.ProgramToPhone.Program other) {
        if (other == com.sdmc.dtv.programsend.ProgramToPhone.Program.getDefaultInstance()) return this;
        if (other.hasId()) {
          setId(other.getId());
        }
        if (other.hasNumber()) {
          setNumber(other.getNumber());
        }
        if (other.hasName()) {
          setName(other.getName());
        }
        if (other.hasType()) {
          setType(other.getType());
        }
        if (other.hasIsScrambler()) {
          setIsScrambler(other.getIsScrambler());
        }
        if (other.hasIsFavor()) {
          setIsFavor(other.getIsFavor());
        }
        if (other.hasIsLock()) {
          setIsLock(other.getIsLock());
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }
      
      public final boolean isInitialized() {
        if (!hasId()) {
          
          return false;
        }
        if (!hasNumber()) {
          
          return false;
        }
        if (!hasName()) {
          
          return false;
        }
        if (!hasType()) {
          
          return false;
        }
        if (!hasIsScrambler()) {
          
          return false;
        }
        if (!hasIsFavor()) {
          
          return false;
        }
        if (!hasIsLock()) {
          
          return false;
        }
        return true;
      }
      
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder(
            this.getUnknownFields());
        while (true) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              this.setUnknownFields(unknownFields.build());
              onChanged();
              return this;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                this.setUnknownFields(unknownFields.build());
                onChanged();
                return this;
              }
              break;
            }
            case 8: {
              bitField0_ |= 0x00000001;
              id_ = input.readInt32();
              break;
            }
            case 18: {
              bitField0_ |= 0x00000002;
              number_ = input.readBytes();
              break;
            }
            case 26: {
              bitField0_ |= 0x00000004;
              name_ = input.readBytes();
              break;
            }
            case 32: {
              bitField0_ |= 0x00000008;
              type_ = input.readInt32();
              break;
            }
            case 40: {
              bitField0_ |= 0x00000010;
              isScrambler_ = input.readBool();
              break;
            }
            case 48: {
              bitField0_ |= 0x00000020;
              isFavor_ = input.readBool();
              break;
            }
            case 56: {
              bitField0_ |= 0x00000040;
              isLock_ = input.readBool();
              break;
            }
          }
        }
      }
      
      private int bitField0_;
      
      // required int32 id = 1;
      private int id_ ;
      public boolean hasId() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }
      public int getId() {
        return id_;
      }
      public Builder setId(int value) {
        bitField0_ |= 0x00000001;
        id_ = value;
        onChanged();
        return this;
      }
      public Builder clearId() {
        bitField0_ = (bitField0_ & ~0x00000001);
        id_ = 0;
        onChanged();
        return this;
      }
      
      // required string number = 2;
      private java.lang.Object number_ = "";
      public boolean hasNumber() {
        return ((bitField0_ & 0x00000002) == 0x00000002);
      }
      public String getNumber() {
        java.lang.Object ref = number_;
        if (!(ref instanceof String)) {
          String s = ((com.google.protobuf.ByteString) ref).toStringUtf8();
          number_ = s;
          return s;
        } else {
          return (String) ref;
        }
      }
      public Builder setNumber(String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000002;
        number_ = value;
        onChanged();
        return this;
      }
      public Builder clearNumber() {
        bitField0_ = (bitField0_ & ~0x00000002);
        number_ = getDefaultInstance().getNumber();
        onChanged();
        return this;
      }
      void setNumber(com.google.protobuf.ByteString value) {
        bitField0_ |= 0x00000002;
        number_ = value;
        onChanged();
      }
      
      // required string name = 3;
      private java.lang.Object name_ = "";
      public boolean hasName() {
        return ((bitField0_ & 0x00000004) == 0x00000004);
      }
      public String getName() {
        java.lang.Object ref = name_;
        if (!(ref instanceof String)) {
          String s = ((com.google.protobuf.ByteString) ref).toStringUtf8();
          name_ = s;
          return s;
        } else {
          return (String) ref;
        }
      }
      public Builder setName(String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000004;
        name_ = value;
        onChanged();
        return this;
      }
      public Builder clearName() {
        bitField0_ = (bitField0_ & ~0x00000004);
        name_ = getDefaultInstance().getName();
        onChanged();
        return this;
      }
      void setName(com.google.protobuf.ByteString value) {
        bitField0_ |= 0x00000004;
        name_ = value;
        onChanged();
      }
      
      // required int32 type = 4;
      private int type_ ;
      public boolean hasType() {
        return ((bitField0_ & 0x00000008) == 0x00000008);
      }
      public int getType() {
        return type_;
      }
      public Builder setType(int value) {
        bitField0_ |= 0x00000008;
        type_ = value;
        onChanged();
        return this;
      }
      public Builder clearType() {
        bitField0_ = (bitField0_ & ~0x00000008);
        type_ = 0;
        onChanged();
        return this;
      }
      
      // required bool isScrambler = 5;
      private boolean isScrambler_ ;
      public boolean hasIsScrambler() {
        return ((bitField0_ & 0x00000010) == 0x00000010);
      }
      public boolean getIsScrambler() {
        return isScrambler_;
      }
      public Builder setIsScrambler(boolean value) {
        bitField0_ |= 0x00000010;
        isScrambler_ = value;
        onChanged();
        return this;
      }
      public Builder clearIsScrambler() {
        bitField0_ = (bitField0_ & ~0x00000010);
        isScrambler_ = false;
        onChanged();
        return this;
      }
      
      // required bool isFavor = 6;
      private boolean isFavor_ ;
      public boolean hasIsFavor() {
        return ((bitField0_ & 0x00000020) == 0x00000020);
      }
      public boolean getIsFavor() {
        return isFavor_;
      }
      public Builder setIsFavor(boolean value) {
        bitField0_ |= 0x00000020;
        isFavor_ = value;
        onChanged();
        return this;
      }
      public Builder clearIsFavor() {
        bitField0_ = (bitField0_ & ~0x00000020);
        isFavor_ = false;
        onChanged();
        return this;
      }
      
      // required bool isLock = 7;
      private boolean isLock_ ;
      public boolean hasIsLock() {
        return ((bitField0_ & 0x00000040) == 0x00000040);
      }
      public boolean getIsLock() {
        return isLock_;
      }
      public Builder setIsLock(boolean value) {
        bitField0_ |= 0x00000040;
        isLock_ = value;
        onChanged();
        return this;
      }
      public Builder clearIsLock() {
        bitField0_ = (bitField0_ & ~0x00000040);
        isLock_ = false;
        onChanged();
        return this;
      }
      
      // @@protoc_insertion_point(builder_scope:com.sdmc.dtv.programsend.Program)
    }
    
    static {
      defaultInstance = new Program(true);
      defaultInstance.initFields();
    }
    
    // @@protoc_insertion_point(class_scope:com.sdmc.dtv.programsend.Program)
  }
  
  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_com_sdmc_dtv_programsend_Program_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_com_sdmc_dtv_programsend_Program_fieldAccessorTable;
  
  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\rprogram.proto\022\030com.sdmc.dtv.programsen" +
      "d\"w\n\007Program\022\n\n\002id\030\001 \002(\005\022\016\n\006number\030\002 \002(\t" +
      "\022\014\n\004name\030\003 \002(\t\022\014\n\004type\030\004 \002(\005\022\023\n\013isScramb" +
      "ler\030\005 \002(\010\022\017\n\007isFavor\030\006 \002(\010\022\016\n\006isLock\030\007 \002" +
      "(\010B*\n\030com.sdmc.dtv.programsendB\016ProgramT" +
      "oPhone"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
      new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
        public com.google.protobuf.ExtensionRegistry assignDescriptors(
            com.google.protobuf.Descriptors.FileDescriptor root) {
          descriptor = root;
          internal_static_com_sdmc_dtv_programsend_Program_descriptor =
            getDescriptor().getMessageTypes().get(0);
          internal_static_com_sdmc_dtv_programsend_Program_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_com_sdmc_dtv_programsend_Program_descriptor,
              new java.lang.String[] { "Id", "Number", "Name", "Type", "IsScrambler", "IsFavor", "IsLock", },
              com.sdmc.dtv.programsend.ProgramToPhone.Program.class,
              com.sdmc.dtv.programsend.ProgramToPhone.Program.Builder.class);
          return null;
        }
      };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
  }
  
  // @@protoc_insertion_point(outer_class_scope)
}
