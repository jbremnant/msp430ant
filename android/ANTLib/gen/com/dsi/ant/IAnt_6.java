/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\Users\\jbkim\\Development\\java\\projects\\ANTLib\\src\\com\\dsi\\ant\\IAnt_6.aidl
 */
package com.dsi.ant;
/**
 * The Android Ant API is not finalized, and *will* be updated and expanded.
 * This is the base level ANT messaging API and gives any application full
 * control over the ANT radio HW.  Caution should be exercised when using
 * this interface.
 *
 * Public API for controlling the Ant Service.
 *
 * {@hide}
 */
public interface IAnt_6 extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.dsi.ant.IAnt_6
{
private static final java.lang.String DESCRIPTOR = "com.dsi.ant.IAnt_6";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.dsi.ant.IAnt_6 interface,
 * generating a proxy if needed.
 */
public static com.dsi.ant.IAnt_6 asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.dsi.ant.IAnt_6))) {
return ((com.dsi.ant.IAnt_6)iin);
}
return new com.dsi.ant.IAnt_6.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_enable:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.enable();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_disable:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.disable();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_isEnabled:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isEnabled();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_ANTTxMessage:
{
data.enforceInterface(DESCRIPTOR);
byte[] _arg0;
_arg0 = data.createByteArray();
boolean _result = this.ANTTxMessage(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_ANTResetSystem:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.ANTResetSystem();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_ANTUnassignChannel:
{
data.enforceInterface(DESCRIPTOR);
byte _arg0;
_arg0 = data.readByte();
boolean _result = this.ANTUnassignChannel(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_ANTAssignChannel:
{
data.enforceInterface(DESCRIPTOR);
byte _arg0;
_arg0 = data.readByte();
byte _arg1;
_arg1 = data.readByte();
byte _arg2;
_arg2 = data.readByte();
boolean _result = this.ANTAssignChannel(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_ANTSetChannelId:
{
data.enforceInterface(DESCRIPTOR);
byte _arg0;
_arg0 = data.readByte();
int _arg1;
_arg1 = data.readInt();
byte _arg2;
_arg2 = data.readByte();
byte _arg3;
_arg3 = data.readByte();
boolean _result = this.ANTSetChannelId(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_ANTSetChannelPeriod:
{
data.enforceInterface(DESCRIPTOR);
byte _arg0;
_arg0 = data.readByte();
int _arg1;
_arg1 = data.readInt();
boolean _result = this.ANTSetChannelPeriod(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_ANTSetChannelRFFreq:
{
data.enforceInterface(DESCRIPTOR);
byte _arg0;
_arg0 = data.readByte();
byte _arg1;
_arg1 = data.readByte();
boolean _result = this.ANTSetChannelRFFreq(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_ANTSetChannelSearchTimeout:
{
data.enforceInterface(DESCRIPTOR);
byte _arg0;
_arg0 = data.readByte();
byte _arg1;
_arg1 = data.readByte();
boolean _result = this.ANTSetChannelSearchTimeout(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_ANTSetLowPriorityChannelSearchTimeout:
{
data.enforceInterface(DESCRIPTOR);
byte _arg0;
_arg0 = data.readByte();
byte _arg1;
_arg1 = data.readByte();
boolean _result = this.ANTSetLowPriorityChannelSearchTimeout(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_ANTSetProximitySearch:
{
data.enforceInterface(DESCRIPTOR);
byte _arg0;
_arg0 = data.readByte();
byte _arg1;
_arg1 = data.readByte();
boolean _result = this.ANTSetProximitySearch(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_ANTSetChannelTxPower:
{
data.enforceInterface(DESCRIPTOR);
byte _arg0;
_arg0 = data.readByte();
byte _arg1;
_arg1 = data.readByte();
boolean _result = this.ANTSetChannelTxPower(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_ANTAddChannelId:
{
data.enforceInterface(DESCRIPTOR);
byte _arg0;
_arg0 = data.readByte();
int _arg1;
_arg1 = data.readInt();
byte _arg2;
_arg2 = data.readByte();
byte _arg3;
_arg3 = data.readByte();
byte _arg4;
_arg4 = data.readByte();
boolean _result = this.ANTAddChannelId(_arg0, _arg1, _arg2, _arg3, _arg4);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_ANTConfigList:
{
data.enforceInterface(DESCRIPTOR);
byte _arg0;
_arg0 = data.readByte();
byte _arg1;
_arg1 = data.readByte();
byte _arg2;
_arg2 = data.readByte();
boolean _result = this.ANTConfigList(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_ANTOpenChannel:
{
data.enforceInterface(DESCRIPTOR);
byte _arg0;
_arg0 = data.readByte();
boolean _result = this.ANTOpenChannel(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_ANTCloseChannel:
{
data.enforceInterface(DESCRIPTOR);
byte _arg0;
_arg0 = data.readByte();
boolean _result = this.ANTCloseChannel(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_ANTRequestMessage:
{
data.enforceInterface(DESCRIPTOR);
byte _arg0;
_arg0 = data.readByte();
byte _arg1;
_arg1 = data.readByte();
boolean _result = this.ANTRequestMessage(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_ANTSendBroadcastData:
{
data.enforceInterface(DESCRIPTOR);
byte _arg0;
_arg0 = data.readByte();
byte[] _arg1;
_arg1 = data.createByteArray();
boolean _result = this.ANTSendBroadcastData(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_ANTSendAcknowledgedData:
{
data.enforceInterface(DESCRIPTOR);
byte _arg0;
_arg0 = data.readByte();
byte[] _arg1;
_arg1 = data.createByteArray();
boolean _result = this.ANTSendAcknowledgedData(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_ANTSendBurstTransferPacket:
{
data.enforceInterface(DESCRIPTOR);
byte _arg0;
_arg0 = data.readByte();
byte[] _arg1;
_arg1 = data.createByteArray();
boolean _result = this.ANTSendBurstTransferPacket(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_ANTSendBurstTransfer:
{
data.enforceInterface(DESCRIPTOR);
byte _arg0;
_arg0 = data.readByte();
byte[] _arg1;
_arg1 = data.createByteArray();
int _result = this.ANTSendBurstTransfer(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_ANTTransmitBurst:
{
data.enforceInterface(DESCRIPTOR);
byte _arg0;
_arg0 = data.readByte();
byte[] _arg1;
_arg1 = data.createByteArray();
int _arg2;
_arg2 = data.readInt();
boolean _arg3;
_arg3 = (0!=data.readInt());
int _result = this.ANTTransmitBurst(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_ANTConfigEventBuffering:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
int _arg2;
_arg2 = data.readInt();
int _arg3;
_arg3 = data.readInt();
boolean _result = this.ANTConfigEventBuffering(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_ANTDisableEventBuffering:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.ANTDisableEventBuffering();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getServiceLibraryVersionCode:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getServiceLibraryVersionCode();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getServiceLibraryVersionName:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getServiceLibraryVersionName();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_claimInterface:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.claimInterface();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_requestForceClaimInterface:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.requestForceClaimInterface(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_stopRequestForceClaimInterface:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.stopRequestForceClaimInterface();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_releaseInterface:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.releaseInterface();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_hasClaimedInterface:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.hasClaimedInterface();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.dsi.ant.IAnt_6
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
// Since version 1 (1.0):

public boolean enable() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_enable, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean disable() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_disable, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean isEnabled() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isEnabled, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean ANTTxMessage(byte[] message) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByteArray(message);
mRemote.transact(Stub.TRANSACTION_ANTTxMessage, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean ANTResetSystem() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_ANTResetSystem, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean ANTUnassignChannel(byte channelNumber) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByte(channelNumber);
mRemote.transact(Stub.TRANSACTION_ANTUnassignChannel, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean ANTAssignChannel(byte channelNumber, byte channelType, byte networkNumber) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByte(channelNumber);
_data.writeByte(channelType);
_data.writeByte(networkNumber);
mRemote.transact(Stub.TRANSACTION_ANTAssignChannel, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean ANTSetChannelId(byte channelNumber, int deviceNumber, byte deviceType, byte txType) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByte(channelNumber);
_data.writeInt(deviceNumber);
_data.writeByte(deviceType);
_data.writeByte(txType);
mRemote.transact(Stub.TRANSACTION_ANTSetChannelId, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean ANTSetChannelPeriod(byte channelNumber, int channelPeriod) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByte(channelNumber);
_data.writeInt(channelPeriod);
mRemote.transact(Stub.TRANSACTION_ANTSetChannelPeriod, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean ANTSetChannelRFFreq(byte channelNumber, byte radioFrequency) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByte(channelNumber);
_data.writeByte(radioFrequency);
mRemote.transact(Stub.TRANSACTION_ANTSetChannelRFFreq, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean ANTSetChannelSearchTimeout(byte channelNumber, byte searchTimeout) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByte(channelNumber);
_data.writeByte(searchTimeout);
mRemote.transact(Stub.TRANSACTION_ANTSetChannelSearchTimeout, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean ANTSetLowPriorityChannelSearchTimeout(byte channelNumber, byte searchTimeout) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByte(channelNumber);
_data.writeByte(searchTimeout);
mRemote.transact(Stub.TRANSACTION_ANTSetLowPriorityChannelSearchTimeout, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean ANTSetProximitySearch(byte channelNumber, byte searchThreshold) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByte(channelNumber);
_data.writeByte(searchThreshold);
mRemote.transact(Stub.TRANSACTION_ANTSetProximitySearch, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean ANTSetChannelTxPower(byte channelNumber, byte txPower) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByte(channelNumber);
_data.writeByte(txPower);
mRemote.transact(Stub.TRANSACTION_ANTSetChannelTxPower, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean ANTAddChannelId(byte channelNumber, int deviceNumber, byte deviceType, byte txType, byte listIndex) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByte(channelNumber);
_data.writeInt(deviceNumber);
_data.writeByte(deviceType);
_data.writeByte(txType);
_data.writeByte(listIndex);
mRemote.transact(Stub.TRANSACTION_ANTAddChannelId, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean ANTConfigList(byte channelNumber, byte listSize, byte exclude) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByte(channelNumber);
_data.writeByte(listSize);
_data.writeByte(exclude);
mRemote.transact(Stub.TRANSACTION_ANTConfigList, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean ANTOpenChannel(byte channelNumber) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByte(channelNumber);
mRemote.transact(Stub.TRANSACTION_ANTOpenChannel, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean ANTCloseChannel(byte channelNumber) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByte(channelNumber);
mRemote.transact(Stub.TRANSACTION_ANTCloseChannel, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean ANTRequestMessage(byte channelNumber, byte messageID) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByte(channelNumber);
_data.writeByte(messageID);
mRemote.transact(Stub.TRANSACTION_ANTRequestMessage, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean ANTSendBroadcastData(byte channelNumber, byte[] txBuffer) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByte(channelNumber);
_data.writeByteArray(txBuffer);
mRemote.transact(Stub.TRANSACTION_ANTSendBroadcastData, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean ANTSendAcknowledgedData(byte channelNumber, byte[] txBuffer) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByte(channelNumber);
_data.writeByteArray(txBuffer);
mRemote.transact(Stub.TRANSACTION_ANTSendAcknowledgedData, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean ANTSendBurstTransferPacket(byte control, byte[] txBuffer) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByte(control);
_data.writeByteArray(txBuffer);
mRemote.transact(Stub.TRANSACTION_ANTSendBurstTransferPacket, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int ANTSendBurstTransfer(byte channelNumber, byte[] txBuffer) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByte(channelNumber);
_data.writeByteArray(txBuffer);
mRemote.transact(Stub.TRANSACTION_ANTSendBurstTransfer, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int ANTTransmitBurst(byte channelNumber, byte[] txBuffer, int initialPacket, boolean containsEndOfBurst) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByte(channelNumber);
_data.writeByteArray(txBuffer);
_data.writeInt(initialPacket);
_data.writeInt(((containsEndOfBurst)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_ANTTransmitBurst, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
// Since version 4 (1.3):

public boolean ANTConfigEventBuffering(int screenOnFlushTimerInterval, int screenOnFlushBufferThreshold, int screenOffFlushTimerInterval, int screenOffFlushBufferThreshold) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(screenOnFlushTimerInterval);
_data.writeInt(screenOnFlushBufferThreshold);
_data.writeInt(screenOffFlushTimerInterval);
_data.writeInt(screenOffFlushBufferThreshold);
mRemote.transact(Stub.TRANSACTION_ANTConfigEventBuffering, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
// Since version 2 (1.1):

public boolean ANTDisableEventBuffering() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_ANTDisableEventBuffering, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
// Since version 3 (1.2):

public int getServiceLibraryVersionCode() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getServiceLibraryVersionCode, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public java.lang.String getServiceLibraryVersionName() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getServiceLibraryVersionName, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
// Since version 6 (1.5):

public boolean claimInterface() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_claimInterface, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean requestForceClaimInterface(java.lang.String appName) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(appName);
mRemote.transact(Stub.TRANSACTION_requestForceClaimInterface, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean stopRequestForceClaimInterface() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_stopRequestForceClaimInterface, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean releaseInterface() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_releaseInterface, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean hasClaimedInterface() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_hasClaimedInterface, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_enable = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_disable = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_isEnabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_ANTTxMessage = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_ANTResetSystem = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_ANTUnassignChannel = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_ANTAssignChannel = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_ANTSetChannelId = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_ANTSetChannelPeriod = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_ANTSetChannelRFFreq = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_ANTSetChannelSearchTimeout = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
static final int TRANSACTION_ANTSetLowPriorityChannelSearchTimeout = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
static final int TRANSACTION_ANTSetProximitySearch = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
static final int TRANSACTION_ANTSetChannelTxPower = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
static final int TRANSACTION_ANTAddChannelId = (android.os.IBinder.FIRST_CALL_TRANSACTION + 14);
static final int TRANSACTION_ANTConfigList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 15);
static final int TRANSACTION_ANTOpenChannel = (android.os.IBinder.FIRST_CALL_TRANSACTION + 16);
static final int TRANSACTION_ANTCloseChannel = (android.os.IBinder.FIRST_CALL_TRANSACTION + 17);
static final int TRANSACTION_ANTRequestMessage = (android.os.IBinder.FIRST_CALL_TRANSACTION + 18);
static final int TRANSACTION_ANTSendBroadcastData = (android.os.IBinder.FIRST_CALL_TRANSACTION + 19);
static final int TRANSACTION_ANTSendAcknowledgedData = (android.os.IBinder.FIRST_CALL_TRANSACTION + 20);
static final int TRANSACTION_ANTSendBurstTransferPacket = (android.os.IBinder.FIRST_CALL_TRANSACTION + 21);
static final int TRANSACTION_ANTSendBurstTransfer = (android.os.IBinder.FIRST_CALL_TRANSACTION + 22);
static final int TRANSACTION_ANTTransmitBurst = (android.os.IBinder.FIRST_CALL_TRANSACTION + 23);
static final int TRANSACTION_ANTConfigEventBuffering = (android.os.IBinder.FIRST_CALL_TRANSACTION + 24);
static final int TRANSACTION_ANTDisableEventBuffering = (android.os.IBinder.FIRST_CALL_TRANSACTION + 25);
static final int TRANSACTION_getServiceLibraryVersionCode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 26);
static final int TRANSACTION_getServiceLibraryVersionName = (android.os.IBinder.FIRST_CALL_TRANSACTION + 27);
static final int TRANSACTION_claimInterface = (android.os.IBinder.FIRST_CALL_TRANSACTION + 28);
static final int TRANSACTION_requestForceClaimInterface = (android.os.IBinder.FIRST_CALL_TRANSACTION + 29);
static final int TRANSACTION_stopRequestForceClaimInterface = (android.os.IBinder.FIRST_CALL_TRANSACTION + 30);
static final int TRANSACTION_releaseInterface = (android.os.IBinder.FIRST_CALL_TRANSACTION + 31);
static final int TRANSACTION_hasClaimedInterface = (android.os.IBinder.FIRST_CALL_TRANSACTION + 32);
}
// Since version 1 (1.0):

public boolean enable() throws android.os.RemoteException;
public boolean disable() throws android.os.RemoteException;
public boolean isEnabled() throws android.os.RemoteException;
public boolean ANTTxMessage(byte[] message) throws android.os.RemoteException;
public boolean ANTResetSystem() throws android.os.RemoteException;
public boolean ANTUnassignChannel(byte channelNumber) throws android.os.RemoteException;
public boolean ANTAssignChannel(byte channelNumber, byte channelType, byte networkNumber) throws android.os.RemoteException;
public boolean ANTSetChannelId(byte channelNumber, int deviceNumber, byte deviceType, byte txType) throws android.os.RemoteException;
public boolean ANTSetChannelPeriod(byte channelNumber, int channelPeriod) throws android.os.RemoteException;
public boolean ANTSetChannelRFFreq(byte channelNumber, byte radioFrequency) throws android.os.RemoteException;
public boolean ANTSetChannelSearchTimeout(byte channelNumber, byte searchTimeout) throws android.os.RemoteException;
public boolean ANTSetLowPriorityChannelSearchTimeout(byte channelNumber, byte searchTimeout) throws android.os.RemoteException;
public boolean ANTSetProximitySearch(byte channelNumber, byte searchThreshold) throws android.os.RemoteException;
public boolean ANTSetChannelTxPower(byte channelNumber, byte txPower) throws android.os.RemoteException;
public boolean ANTAddChannelId(byte channelNumber, int deviceNumber, byte deviceType, byte txType, byte listIndex) throws android.os.RemoteException;
public boolean ANTConfigList(byte channelNumber, byte listSize, byte exclude) throws android.os.RemoteException;
public boolean ANTOpenChannel(byte channelNumber) throws android.os.RemoteException;
public boolean ANTCloseChannel(byte channelNumber) throws android.os.RemoteException;
public boolean ANTRequestMessage(byte channelNumber, byte messageID) throws android.os.RemoteException;
public boolean ANTSendBroadcastData(byte channelNumber, byte[] txBuffer) throws android.os.RemoteException;
public boolean ANTSendAcknowledgedData(byte channelNumber, byte[] txBuffer) throws android.os.RemoteException;
public boolean ANTSendBurstTransferPacket(byte control, byte[] txBuffer) throws android.os.RemoteException;
public int ANTSendBurstTransfer(byte channelNumber, byte[] txBuffer) throws android.os.RemoteException;
public int ANTTransmitBurst(byte channelNumber, byte[] txBuffer, int initialPacket, boolean containsEndOfBurst) throws android.os.RemoteException;
// Since version 4 (1.3):

public boolean ANTConfigEventBuffering(int screenOnFlushTimerInterval, int screenOnFlushBufferThreshold, int screenOffFlushTimerInterval, int screenOffFlushBufferThreshold) throws android.os.RemoteException;
// Since version 2 (1.1):

public boolean ANTDisableEventBuffering() throws android.os.RemoteException;
// Since version 3 (1.2):

public int getServiceLibraryVersionCode() throws android.os.RemoteException;
public java.lang.String getServiceLibraryVersionName() throws android.os.RemoteException;
// Since version 6 (1.5):

public boolean claimInterface() throws android.os.RemoteException;
public boolean requestForceClaimInterface(java.lang.String appName) throws android.os.RemoteException;
public boolean stopRequestForceClaimInterface() throws android.os.RemoteException;
public boolean releaseInterface() throws android.os.RemoteException;
public boolean hasClaimedInterface() throws android.os.RemoteException;
}
