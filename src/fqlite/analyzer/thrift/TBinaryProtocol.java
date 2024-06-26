package fqlite.analyzer.thrift;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TList;
import org.apache.thrift.protocol.TMap;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.protocol.TSet;
import org.apache.thrift.protocol.TStruct;
import org.apache.thrift.protocol.TType;
import org.apache.thrift.transport.TTransport;

/**
 * Binary protocol implementation for thrift.
 *
 */
public class TBinaryProtocol extends TProtocol {
  private static final TStruct ANONYMOUS_STRUCT = new TStruct();

  protected static final int VERSION_MASK = 0xffff0000;
  protected static final int VERSION_1 = 0x80010000;

  protected boolean strictRead_ = false;
  protected boolean strictWrite_ = true;

  protected int readLength_;
  protected boolean checkReadLength_ = false;

  /**
   * Factory
   */
  public static class Factory implements TProtocolFactory {
    private static final long serialVersionUID = 1L;
	protected boolean strictRead_ = false;
    protected boolean strictWrite_ = true;

    public Factory() {
      this(false, true);
    }

    public Factory(boolean strictRead, boolean strictWrite) {
      strictRead_ = strictRead;
      strictWrite_ = strictWrite;
    }

    public TProtocol getProtocol(TTransport trans) {
      return new TBinaryProtocol(trans, strictRead_, strictWrite_);
    }
  }

  /**
   * Constructor
   */
  public TBinaryProtocol(TTransport trans) {
    this(trans, false, true);
  }

  public TBinaryProtocol(TTransport trans, boolean strictRead, boolean strictWrite) {
    super(trans);
    strictRead_ = strictRead;
    strictWrite_ = strictWrite;
  }

  public void writeMessageBegin(TMessage message) throws TException {
    if (strictWrite_) {
      int version = VERSION_1 | message.type;
      writeI32(version);
      writeString(message.name);
      writeI32(message.seqid);
    } else {
      writeString(message.name);
      writeByte(message.type);
      writeI32(message.seqid);
    }
  }

  public void writeMessageEnd() {}

  public void writeStructBegin(TStruct struct) {}

  public void writeStructEnd() {}

  public void writeFieldBegin(TField field) throws TException {
    writeByte(field.type);
    writeI16(field.id);
  }

  public void writeFieldEnd() {}

  public void writeFieldStop() throws TException {
    writeByte(TType.STOP);
  }

  public void writeMapBegin(TMap map) throws TException {
    writeByte(map.keyType);
    writeByte(map.valueType);
    writeI32(map.size);
  }

  public void writeMapEnd() {}

  public void writeListBegin(TList list) throws TException {
    writeByte(list.elemType);
    writeI32(list.size);
  }

  public void writeListEnd() {}

  public void writeSetBegin(TSet set) throws TException {
    writeByte(set.elemType);
    writeI32(set.size);
  }

  public void writeSetEnd() {}

  public void writeBool(boolean b) throws TException {
    writeByte(b ? (byte)1 : (byte)0);
  }

  private byte [] bout = new byte[1];
  public void writeByte(byte b) throws TException {
    bout[0] = b;
    trans_.write(bout, 0, 1);
  }

  private byte[] i16out = new byte[2];
  public void writeI16(short i16) throws TException {
    i16out[0] = (byte)(0xff & (i16 >> 8));
    i16out[1] = (byte)(0xff & (i16));
    trans_.write(i16out, 0, 2);
  }

  private byte[] i32out = new byte[4];
  public void writeI32(int i32) throws TException {
    i32out[0] = (byte)(0xff & (i32 >> 24));
    i32out[1] = (byte)(0xff & (i32 >> 16));
    i32out[2] = (byte)(0xff & (i32 >> 8));
    i32out[3] = (byte)(0xff & (i32));
    trans_.write(i32out, 0, 4);
  }

  private byte[] i64out = new byte[8];
  public void writeI64(long i64) throws TException {
    i64out[0] = (byte)(0xff & (i64 >> 56));
    i64out[1] = (byte)(0xff & (i64 >> 48));
    i64out[2] = (byte)(0xff & (i64 >> 40));
    i64out[3] = (byte)(0xff & (i64 >> 32));
    i64out[4] = (byte)(0xff & (i64 >> 24));
    i64out[5] = (byte)(0xff & (i64 >> 16));
    i64out[6] = (byte)(0xff & (i64 >> 8));
    i64out[7] = (byte)(0xff & (i64));
    trans_.write(i64out, 0, 8);
  }

  public void writeDouble(double dub) throws TException {
    writeI64(Double.doubleToLongBits(dub));
  }

  public void writeString(String str) throws TException {
    try {
      byte[] dat = str.getBytes("UTF-8");
      writeI32(dat.length);
      trans_.write(dat, 0, dat.length);
    } catch (UnsupportedEncodingException uex) {
      throw new TException("JVM DOES NOT SUPPORT UTF-8");
    }
  }

  public void writeBinary(byte[] bin) throws TException {
    writeI32(bin.length);
    trans_.write(bin, 0, bin.length);
  }

  /**
   * Reading methods.
   */

  public TMessage readMessageBegin() throws TException {
    int size = readI32();
    if (size < 0) {
      int version = size & VERSION_MASK;
      if (version != VERSION_1) {
        throw new TProtocolException(TProtocolException.BAD_VERSION, "Bad version in readMessageBegin");
      }
      return new TMessage(readString(), (byte)(size & 0x000000ff), readI32());
    } else {
      if (strictRead_) {
        throw new TProtocolException(TProtocolException.BAD_VERSION, "Missing version in readMessageBegin, old client?");
      }
      return new TMessage(readStringBody(size), readByte(), readI32());
    }
  }

  public void readMessageEnd() {}

  public TStruct readStructBegin() {
    return ANONYMOUS_STRUCT;
  }

  public void readStructEnd() {}

  public TField readFieldBegin() throws TException {
    byte type = readByte();
    short id = type == TType.STOP ? 0 : readI16();
    return new TField("", type, id);
  }

  public void readFieldEnd() {}

  public TMap readMapBegin() throws TException {
    return new TMap(readByte(), readByte(), readI32());
  }

  public void readMapEnd() {}

  public TList readListBegin() throws TException {
    return new TList(readByte(), readI32());
  }

  public void readListEnd() {}

  public TSet readSetBegin() throws TException {
    return new TSet(readByte(), readI32());
  }

  public void readSetEnd() {}

  public boolean readBool() throws TException {
    return (readByte() == 1);
  }

  private byte[] bin = new byte[1];
  public byte readByte() throws TException {
    readAll(bin, 0, 1);
    return bin[0];
  }

  private byte[] i16rd = new byte[2];
  public short readI16() throws TException {
    readAll(i16rd, 0, 2);
    return
      (short)
      (((i16rd[0] & 0xff) << 8) |
       ((i16rd[1] & 0xff)));
  }

  private byte[] i32rd = new byte[4];
  public int readI32() throws TException {
    readAll(i32rd, 0, 4);
    return
      ((i32rd[0] & 0xff) << 24) |
      ((i32rd[1] & 0xff) << 16) |
      ((i32rd[2] & 0xff) <<  8) |
      ((i32rd[3] & 0xff));
  }

  private byte[] i64rd = new byte[8];
  public long readI64() throws TException {
    readAll(i64rd, 0, 8);
    return
      ((long)(i64rd[0] & 0xff) << 56) |
      ((long)(i64rd[1] & 0xff) << 48) |
      ((long)(i64rd[2] & 0xff) << 40) |
      ((long)(i64rd[3] & 0xff) << 32) |
      ((long)(i64rd[4] & 0xff) << 24) |
      ((long)(i64rd[5] & 0xff) << 16) |
      ((long)(i64rd[6] & 0xff) <<  8) |
      ((long)(i64rd[7] & 0xff));
  }

  public double readDouble() throws TException {
    return Double.longBitsToDouble(readI64());
  }

  public String readString() throws TException {
    int size = readI32();
    return readStringBody(size);
  }

  public String readStringBody(int size) throws TException {
    try {
      checkReadLength(size);
      byte[] buf = new byte[size];
      trans_.readAll(buf, 0, size);
      return new String(buf, "UTF-8");
    } catch (UnsupportedEncodingException uex) {
      throw new TException("JVM DOES NOT SUPPORT UTF-8");
    }
  }

  public byte[] readBinary1() throws TException {
    int size = readI32();
   checkReadLength(size);
    byte[] buf = new byte[size];
    trans_.readAll(buf, 0, size);
    return buf;
  }

  private int readAll(byte[] buf, int off, int len) throws TException {
    checkReadLength(len);
    return trans_.readAll(buf, off, len);
  }

  public void setReadLength(int readLength) {
    readLength_ = readLength;
    checkReadLength_ = true;
  }

  protected void checkReadLength(int length) throws TException {
    if (checkReadLength_) {
      readLength_ -= length;
      if (readLength_ < 0) {
        throw new TException("Message length exceeded: " + length);
      }
    }
  }

@Override
public void writeBinary(ByteBuffer arg0) throws TException {
	// TODO Auto-generated method stub
	
}

@Override
public ByteBuffer readBinary() throws TException {
	
	byte[] bb = readBinary1();
	return ByteBuffer.wrap(bb);
}

}
