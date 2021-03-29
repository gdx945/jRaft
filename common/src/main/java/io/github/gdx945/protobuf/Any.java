// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: any.proto

package io.github.gdx945.protobuf;

public final class Any {
  private Any() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface anyOrBuilder extends
      // @@protoc_insertion_point(interface_extends:any)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>bool boolField = 1;</code>
     * @return Whether the boolField field is set.
     */
    boolean hasBoolField();
    /**
     * <code>bool boolField = 1;</code>
     * @return The boolField.
     */
    boolean getBoolField();

    /**
     * <code>double doubleField = 2;</code>
     * @return Whether the doubleField field is set.
     */
    boolean hasDoubleField();
    /**
     * <code>double doubleField = 2;</code>
     * @return The doubleField.
     */
    double getDoubleField();

    /**
     * <code>int64 longField = 3;</code>
     * @return Whether the longField field is set.
     */
    boolean hasLongField();
    /**
     * <code>int64 longField = 3;</code>
     * @return The longField.
     */
    long getLongField();

    /**
     * <code>string stringField = 4;</code>
     * @return Whether the stringField field is set.
     */
    boolean hasStringField();
    /**
     * <code>string stringField = 4;</code>
     * @return The stringField.
     */
    String getStringField();
    /**
     * <code>string stringField = 4;</code>
     * @return The bytes for stringField.
     */
    com.google.protobuf.ByteString
        getStringFieldBytes();

    public any.AnyOneofCase getAnyOneofCase();
  }
  /**
   * Protobuf type {@code any}
   */
  public static final class any extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:any)
      anyOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use any.newBuilder() to construct.
    private any(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private any() {
    }

    @Override
    @SuppressWarnings({"unused"})
    protected Object newInstance(
        UnusedPrivateParameter unused) {
      return new any();
    }

    @Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private any(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
        throw new NullPointerException();
      }
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            case 8: {
              anyOneofCase_ = 1;
              anyOneof_ = input.readBool();
              break;
            }
            case 17: {
              anyOneofCase_ = 2;
              anyOneof_ = input.readDouble();
              break;
            }
            case 24: {
              anyOneofCase_ = 3;
              anyOneof_ = input.readInt64();
              break;
            }
            case 34: {
              String s = input.readStringRequireUtf8();
              anyOneofCase_ = 4;
              anyOneof_ = s;
              break;
            }
            default: {
              if (!parseUnknownField(
                  input, unknownFields, extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return Any.internal_static_any_descriptor;
    }

    @Override
    protected FieldAccessorTable
        internalGetFieldAccessorTable() {
      return Any.internal_static_any_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              any.class, Builder.class);
    }

    private int anyOneofCase_ = 0;
    private Object anyOneof_;
    public enum AnyOneofCase
        implements com.google.protobuf.Internal.EnumLite,
            InternalOneOfEnum {
      BOOLFIELD(1),
      DOUBLEFIELD(2),
      LONGFIELD(3),
      STRINGFIELD(4),
      ANYONEOF_NOT_SET(0);
      private final int value;
      private AnyOneofCase(int value) {
        this.value = value;
      }
      /**
       * @param value The number of the enum to look for.
       * @return The enum associated with the given number.
       * @deprecated Use {@link #forNumber(int)} instead.
       */
      @Deprecated
      public static AnyOneofCase valueOf(int value) {
        return forNumber(value);
      }

      public static AnyOneofCase forNumber(int value) {
        switch (value) {
          case 1: return BOOLFIELD;
          case 2: return DOUBLEFIELD;
          case 3: return LONGFIELD;
          case 4: return STRINGFIELD;
          case 0: return ANYONEOF_NOT_SET;
          default: return null;
        }
      }
      public int getNumber() {
        return this.value;
      }
    };

    public AnyOneofCase
    getAnyOneofCase() {
      return AnyOneofCase.forNumber(
          anyOneofCase_);
    }

    public static final int BOOLFIELD_FIELD_NUMBER = 1;
    /**
     * <code>bool boolField = 1;</code>
     * @return Whether the boolField field is set.
     */
    @Override
    public boolean hasBoolField() {
      return anyOneofCase_ == 1;
    }
    /**
     * <code>bool boolField = 1;</code>
     * @return The boolField.
     */
    @Override
    public boolean getBoolField() {
      if (anyOneofCase_ == 1) {
        return (Boolean) anyOneof_;
      }
      return false;
    }

    public static final int DOUBLEFIELD_FIELD_NUMBER = 2;
    /**
     * <code>double doubleField = 2;</code>
     * @return Whether the doubleField field is set.
     */
    @Override
    public boolean hasDoubleField() {
      return anyOneofCase_ == 2;
    }
    /**
     * <code>double doubleField = 2;</code>
     * @return The doubleField.
     */
    @Override
    public double getDoubleField() {
      if (anyOneofCase_ == 2) {
        return (Double) anyOneof_;
      }
      return 0D;
    }

    public static final int LONGFIELD_FIELD_NUMBER = 3;
    /**
     * <code>int64 longField = 3;</code>
     * @return Whether the longField field is set.
     */
    @Override
    public boolean hasLongField() {
      return anyOneofCase_ == 3;
    }
    /**
     * <code>int64 longField = 3;</code>
     * @return The longField.
     */
    @Override
    public long getLongField() {
      if (anyOneofCase_ == 3) {
        return (Long) anyOneof_;
      }
      return 0L;
    }

    public static final int STRINGFIELD_FIELD_NUMBER = 4;
    /**
     * <code>string stringField = 4;</code>
     * @return Whether the stringField field is set.
     */
    public boolean hasStringField() {
      return anyOneofCase_ == 4;
    }
    /**
     * <code>string stringField = 4;</code>
     * @return The stringField.
     */
    public String getStringField() {
      Object ref = "";
      if (anyOneofCase_ == 4) {
        ref = anyOneof_;
      }
      if (ref instanceof String) {
        return (String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        if (anyOneofCase_ == 4) {
          anyOneof_ = s;
        }
        return s;
      }
    }
    /**
     * <code>string stringField = 4;</code>
     * @return The bytes for stringField.
     */
    public com.google.protobuf.ByteString
        getStringFieldBytes() {
      Object ref = "";
      if (anyOneofCase_ == 4) {
        ref = anyOneof_;
      }
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        if (anyOneofCase_ == 4) {
          anyOneof_ = b;
        }
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    private byte memoizedIsInitialized = -1;
    @Override
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    @Override
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (anyOneofCase_ == 1) {
        output.writeBool(
            1, (boolean)((Boolean) anyOneof_));
      }
      if (anyOneofCase_ == 2) {
        output.writeDouble(
            2, (double)((Double) anyOneof_));
      }
      if (anyOneofCase_ == 3) {
        output.writeInt64(
            3, (long)((Long) anyOneof_));
      }
      if (anyOneofCase_ == 4) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 4, anyOneof_);
      }
      unknownFields.writeTo(output);
    }

    @Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (anyOneofCase_ == 1) {
        size += com.google.protobuf.CodedOutputStream
          .computeBoolSize(
              1, (boolean)((Boolean) anyOneof_));
      }
      if (anyOneofCase_ == 2) {
        size += com.google.protobuf.CodedOutputStream
          .computeDoubleSize(
              2, (double)((Double) anyOneof_));
      }
      if (anyOneofCase_ == 3) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(
              3, (long)((Long) anyOneof_));
      }
      if (anyOneofCase_ == 4) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(4, anyOneof_);
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @Override
    public boolean equals(final Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof any)) {
        return super.equals(obj);
      }
      any other = (any) obj;

      if (!getAnyOneofCase().equals(other.getAnyOneofCase())) return false;
      switch (anyOneofCase_) {
        case 1:
          if (getBoolField()
              != other.getBoolField()) return false;
          break;
        case 2:
          if (Double.doubleToLongBits(getDoubleField())
              != Double.doubleToLongBits(
                  other.getDoubleField())) return false;
          break;
        case 3:
          if (getLongField()
              != other.getLongField()) return false;
          break;
        case 4:
          if (!getStringField()
              .equals(other.getStringField())) return false;
          break;
        case 0:
        default:
      }
      if (!unknownFields.equals(other.unknownFields)) return false;
      return true;
    }

    @Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      switch (anyOneofCase_) {
        case 1:
          hash = (37 * hash) + BOOLFIELD_FIELD_NUMBER;
          hash = (53 * hash) + com.google.protobuf.Internal.hashBoolean(
              getBoolField());
          break;
        case 2:
          hash = (37 * hash) + DOUBLEFIELD_FIELD_NUMBER;
          hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
              Double.doubleToLongBits(getDoubleField()));
          break;
        case 3:
          hash = (37 * hash) + LONGFIELD_FIELD_NUMBER;
          hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
              getLongField());
          break;
        case 4:
          hash = (37 * hash) + STRINGFIELD_FIELD_NUMBER;
          hash = (53 * hash) + getStringField().hashCode();
          break;
        case 0:
        default:
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static any parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static any parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static any parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static any parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static any parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static any parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static any parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static any parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static any parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static any parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static any parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static any parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @Override
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(any prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    @Override
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @Override
    protected Builder newBuilderForType(
        BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code any}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:any)
        anyOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return Any.internal_static_any_descriptor;
      }

      @Override
      protected FieldAccessorTable
          internalGetFieldAccessorTable() {
        return Any.internal_static_any_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                any.class, Builder.class);
      }

      // Construct using io.github.gdx945.protobuf.Any.any.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
        }
      }
      @Override
      public Builder clear() {
        super.clear();
        anyOneofCase_ = 0;
        anyOneof_ = null;
        return this;
      }

      @Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return Any.internal_static_any_descriptor;
      }

      @Override
      public any getDefaultInstanceForType() {
        return any.getDefaultInstance();
      }

      @Override
      public any build() {
        any result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @Override
      public any buildPartial() {
        any result = new any(this);
        if (anyOneofCase_ == 1) {
          result.anyOneof_ = anyOneof_;
        }
        if (anyOneofCase_ == 2) {
          result.anyOneof_ = anyOneof_;
        }
        if (anyOneofCase_ == 3) {
          result.anyOneof_ = anyOneof_;
        }
        if (anyOneofCase_ == 4) {
          result.anyOneof_ = anyOneof_;
        }
        result.anyOneofCase_ = anyOneofCase_;
        onBuilt();
        return result;
      }

      @Override
      public Builder clone() {
        return super.clone();
      }
      @Override
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return super.setField(field, value);
      }
      @Override
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return super.clearField(field);
      }
      @Override
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return super.clearOneof(oneof);
      }
      @Override
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, Object value) {
        return super.setRepeatedField(field, index, value);
      }
      @Override
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return super.addRepeatedField(field, value);
      }
      @Override
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof any) {
          return mergeFrom((any)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(any other) {
        if (other == any.getDefaultInstance()) return this;
        switch (other.getAnyOneofCase()) {
          case BOOLFIELD: {
            setBoolField(other.getBoolField());
            break;
          }
          case DOUBLEFIELD: {
            setDoubleField(other.getDoubleField());
            break;
          }
          case LONGFIELD: {
            setLongField(other.getLongField());
            break;
          }
          case STRINGFIELD: {
            anyOneofCase_ = 4;
            anyOneof_ = other.anyOneof_;
            onChanged();
            break;
          }
          case ANYONEOF_NOT_SET: {
            break;
          }
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      @Override
      public final boolean isInitialized() {
        return true;
      }

      @Override
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        any parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (any) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int anyOneofCase_ = 0;
      private Object anyOneof_;
      public AnyOneofCase
          getAnyOneofCase() {
        return AnyOneofCase.forNumber(
            anyOneofCase_);
      }

      public Builder clearAnyOneof() {
        anyOneofCase_ = 0;
        anyOneof_ = null;
        onChanged();
        return this;
      }


      /**
       * <code>bool boolField = 1;</code>
       * @return Whether the boolField field is set.
       */
      public boolean hasBoolField() {
        return anyOneofCase_ == 1;
      }
      /**
       * <code>bool boolField = 1;</code>
       * @return The boolField.
       */
      public boolean getBoolField() {
        if (anyOneofCase_ == 1) {
          return (Boolean) anyOneof_;
        }
        return false;
      }
      /**
       * <code>bool boolField = 1;</code>
       * @param value The boolField to set.
       * @return This builder for chaining.
       */
      public Builder setBoolField(boolean value) {
        anyOneofCase_ = 1;
        anyOneof_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>bool boolField = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearBoolField() {
        if (anyOneofCase_ == 1) {
          anyOneofCase_ = 0;
          anyOneof_ = null;
          onChanged();
        }
        return this;
      }

      /**
       * <code>double doubleField = 2;</code>
       * @return Whether the doubleField field is set.
       */
      public boolean hasDoubleField() {
        return anyOneofCase_ == 2;
      }
      /**
       * <code>double doubleField = 2;</code>
       * @return The doubleField.
       */
      public double getDoubleField() {
        if (anyOneofCase_ == 2) {
          return (Double) anyOneof_;
        }
        return 0D;
      }
      /**
       * <code>double doubleField = 2;</code>
       * @param value The doubleField to set.
       * @return This builder for chaining.
       */
      public Builder setDoubleField(double value) {
        anyOneofCase_ = 2;
        anyOneof_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>double doubleField = 2;</code>
       * @return This builder for chaining.
       */
      public Builder clearDoubleField() {
        if (anyOneofCase_ == 2) {
          anyOneofCase_ = 0;
          anyOneof_ = null;
          onChanged();
        }
        return this;
      }

      /**
       * <code>int64 longField = 3;</code>
       * @return Whether the longField field is set.
       */
      public boolean hasLongField() {
        return anyOneofCase_ == 3;
      }
      /**
       * <code>int64 longField = 3;</code>
       * @return The longField.
       */
      public long getLongField() {
        if (anyOneofCase_ == 3) {
          return (Long) anyOneof_;
        }
        return 0L;
      }
      /**
       * <code>int64 longField = 3;</code>
       * @param value The longField to set.
       * @return This builder for chaining.
       */
      public Builder setLongField(long value) {
        anyOneofCase_ = 3;
        anyOneof_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int64 longField = 3;</code>
       * @return This builder for chaining.
       */
      public Builder clearLongField() {
        if (anyOneofCase_ == 3) {
          anyOneofCase_ = 0;
          anyOneof_ = null;
          onChanged();
        }
        return this;
      }

      /**
       * <code>string stringField = 4;</code>
       * @return Whether the stringField field is set.
       */
      @Override
      public boolean hasStringField() {
        return anyOneofCase_ == 4;
      }
      /**
       * <code>string stringField = 4;</code>
       * @return The stringField.
       */
      @Override
      public String getStringField() {
        Object ref = "";
        if (anyOneofCase_ == 4) {
          ref = anyOneof_;
        }
        if (!(ref instanceof String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          String s = bs.toStringUtf8();
          if (anyOneofCase_ == 4) {
            anyOneof_ = s;
          }
          return s;
        } else {
          return (String) ref;
        }
      }
      /**
       * <code>string stringField = 4;</code>
       * @return The bytes for stringField.
       */
      @Override
      public com.google.protobuf.ByteString
          getStringFieldBytes() {
        Object ref = "";
        if (anyOneofCase_ == 4) {
          ref = anyOneof_;
        }
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (String) ref);
          if (anyOneofCase_ == 4) {
            anyOneof_ = b;
          }
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string stringField = 4;</code>
       * @param value The stringField to set.
       * @return This builder for chaining.
       */
      public Builder setStringField(
          String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  anyOneofCase_ = 4;
        anyOneof_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>string stringField = 4;</code>
       * @return This builder for chaining.
       */
      public Builder clearStringField() {
        if (anyOneofCase_ == 4) {
          anyOneofCase_ = 0;
          anyOneof_ = null;
          onChanged();
        }
        return this;
      }
      /**
       * <code>string stringField = 4;</code>
       * @param value The bytes for stringField to set.
       * @return This builder for chaining.
       */
      public Builder setStringFieldBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        anyOneofCase_ = 4;
        anyOneof_ = value;
        onChanged();
        return this;
      }
      @Override
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      @Override
      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:any)
    }

    // @@protoc_insertion_point(class_scope:any)
    private static final any DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new any();
    }

    public static any getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<any>
        PARSER = new com.google.protobuf.AbstractParser<any>() {
      @Override
      public any parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new any(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<any> parser() {
      return PARSER;
    }

    @Override
    public com.google.protobuf.Parser<any> getParserForType() {
      return PARSER;
    }

    @Override
    public any getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_any_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_any_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    String[] descriptorData = {
      "\n\tany.proto\"j\n\003any\022\023\n\tboolField\030\001 \001(\010H\000\022" +
      "\025\n\013doubleField\030\002 \001(\001H\000\022\023\n\tlongField\030\003 \001(" +
      "\003H\000\022\025\n\013stringField\030\004 \001(\tH\000B\013\n\tany_oneofB" +
      "\033\n\031io.github.gdx945.protobufb\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_any_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_any_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_any_descriptor,
        new String[] { "BoolField", "DoubleField", "LongField", "StringField", "AnyOneof", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
