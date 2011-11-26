/*
 * Copyright 2010 Dynastream Innovations Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.dsi.ant;

import java.util.Arrays;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.dsi.ant.exception.*;

/**
 * Public API for controlling the ANT Radio Service. AntInterface is a proxy
 * object for controlling the ANT Radio Service via IPC. Creating an 
 * AntInterface object will create a binding with the ANT Radio Service.
 * It is recommended you have only one instance, managed from within a local 
 * long-running service (to prevent any conflicts during ANT configuration).
 * 
 * @hide
 */
public class AntInterface {

    // Constants

    /** The Log Tag. */
    public static final String TAG = "ANTInterface";

    /** Enable debug logging. */
    private static final boolean DEBUG = false;

    /** Search string to find ANT Radio Service in the Android Marketplace */
    private static final String RADIO_SERVICE_APP_NAME = "com.dsi.ant.service.socket";

    /** Name of the ANT Radio shared library */
    private static final String ANT_LIBRARY_NAME = "com.dsi.ant.antradio_library";

    // Static variables

    /** The version code (eg. 1) of ANTLib used by the ANT Radio Service */
    private static int mServiceLibraryVersionCode = 0;

    /** Has support for ANT already been checked */
    private static boolean mCheckedAntSupported = false;

    /** Is ANT supported on this device */
    private static boolean mAntSupported = false;

    /** For thread-safe static hasAntSupport() function */
    private static Object CHECK_ANT_SUPPORTED_LOCK = new Object();

    /** Make getInstance() thread-safe. */
    private static Object INSTANCE_LOCK = new Object();

    
    // Instance variables

    /** Inter-process communication with the ANT Radio Service. */
    private IAnt_6 mAntReceiver = null;

    /** The context to use for binding/unbinding the ANT Radio Service. */
    private Context mContext;

    /** Listens to changes to service connection status. */
    private ServiceListener mServiceListener;

    /** Is the ANT Radio Service connected. */
    private boolean mServiceConnected = false;

    // Interface definitions

    /**
     * An interface for notifying AntInterface clients when they have
     * been connected to the ANT Radio Service.
     */
    public interface ServiceListener 
    {
        /**
         * Called to notify the client when it has been
         * connected to the ANT Radio Service. Clients must wait for
         * this callback before making calls on the AntInterface.
         */
        public void onServiceConnected();

        /**
         * Called to notify the client that it has been
         * disconnected from the ANT Radio Service. Clients must not
         * make calls on the AntInterface until the service is
         * reconnected.
         */
        public void onServiceDisconnected();
    }


    // Constructor

    /**
     * Instantiates a new ANT Interface.
     *
     * @param context The context this instance will use, must not be null.
     * @param listener the callback used to notify of status changes to this 
     * service connection. 
     *
     * @since 3.0
     */
    public AntInterface()
    {
        // Initialise variables on each initService
    }

    protected void finalize() throws Throwable 
    {
        try
        {
            releaseService();
        }
        finally
        {
            super.finalize();
        }
    }

    /**
     * Gets the single instance of AntInterface, creating it if it doesn't exist.
     * Only the initial request for an instance will have context and listener set to the requested objects.
     * 
     * Note, since version 3.0 this will always create a new instance.
     *
     * @param context the context used to bind to the remote service.
     * @param listener the listener to be notified of status changes.
     * 
     * @return the AntInterface instance.
     * 
     * @since 1.0
     * 
     * @deprecated Use constructor to create a new instance and initService() 
     * to tell it to start connection to the ANT Radio Service.
     */
    @Deprecated
    public static AntInterface getInstance(Context context,ServiceListener listener) 
    {
        if(DEBUG)   Log.d(TAG, "getInstance");

        synchronized (INSTANCE_LOCK) 
        {
            AntInterface instance = null;

            if(DEBUG)   Log.d(TAG, "getInstance: Creating new instance");

            instance = new AntInterface();

            if(!instance.initService(context, listener))
            {
                Log.e(TAG, "getInstance: No connection to proxy service");

                instance.releaseService();
                instance = null;
            }

            return instance;
        }
    }


    
    /**
     * Go to market.
     *
     * @param pContext the context
     * @since 1.2
     */
    public static void goToMarket(Context pContext)
    {
        if(DEBUG) Log.d(TAG, "goToMarket");

        Intent goToMarket = null;
        goToMarket = new Intent(Intent.ACTION_VIEW,Uri.parse("http://market.android.com/details?id=" + RADIO_SERVICE_APP_NAME));
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        pContext.startActivity(goToMarket);
    }

    /**
     * Class for monitoring the service connection.
     */
    private ServiceConnection mIAntConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName pClassName, IBinder pService) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  We are communicating with our
            // service through an IDL interface, so get a client-side
            // representation of that from the raw service object.
            if(DEBUG)   Log.d(TAG, "mIAntConnection onServiceConnected()");
            mAntReceiver = IAnt_6.Stub.asInterface(pService);

            mServiceConnected = true;

            // Notify the attached application if it is registered
            if (mServiceListener != null) 
            {
                mServiceListener.onServiceConnected();
            }
            else
            {
                if(DEBUG) Log.d(TAG, "mIAntConnection onServiceConnected: No service listener registered");
            }
        }

        public void onServiceDisconnected(ComponentName pClassName) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            if(DEBUG)   Log.d(TAG, "mIAntConnection onServiceDisconnected()");

            mServiceConnected = false;
            mServiceLibraryVersionCode = 0;

            mAntReceiver = null;

            // Notify the attached application if it is registered
            if (mServiceListener != null) 
            {
                mServiceListener.onServiceDisconnected();
            }
            else
            {
                if(DEBUG) Log.d(TAG, "sIAntConnection onServiceDisconnected: No service listener registered");
            }

            // Try and rebind to the service
            Context oldContext = mContext;
            ServiceListener oldServiceListener = mServiceListener;
            releaseService();
            initService(oldContext, oldServiceListener);
        }
    };

    /**
     * Binds this activity to the ANT Radio Service.
     *
     * @return true, if successful
     * 
     * @since 3.0
     */
    public boolean initService(Context context, ServiceListener listener) {
        if(DEBUG)   Log.d(TAG, "initService() entered");

        boolean ret = false;

        if(null == context)
        {
            throw new IllegalArgumentException("Context must be provided");
        }

        if(mServiceConnected)
        {
            if(DEBUG) Log.w(TAG, "initService: Existing connection to service exists, closing.");
            releaseService();
        }
        
        mContext = context;
        mServiceListener = listener;

        ret = mContext.bindService(new Intent(IAnt_6.class.getName()), mIAntConnection, Context.BIND_AUTO_CREATE);
        if(DEBUG) Log.i(TAG, "initService(): Bound with ANT Radio Service: " + ret);

        return ret;
    }

    /** 
     * Unbinds this activity from the ANT Radio Service.
     * 
     *  @since 3.0
     */
    public void releaseService() {
        if(DEBUG)   Log.d(TAG, "releaseService() entered");

        try
        {
            if(null != mContext)
            {
                mContext.unbindService(mIAntConnection);
            }

            mServiceConnected = false;
            mServiceListener = null;
            mContext = null;

            if(DEBUG) Log.d(TAG, "releaseService(): Service unbound.");
        }
        catch(IllegalArgumentException e)
        {
        	if(DEBUG) Log.d(TAG, "releaseService(): Service already unbound.");
        }
    }


    /**
     * True if this activity can communicate with the ANT Radio Service.
     *
     * @return true, if service is connected
     * @since 1.2
     */
    public boolean isServiceConnected()
    {
        return mServiceConnected;
    }


    /**
     * Unbind from the ANT Radio Service.
     *
     * @return true, if successful
     * @since 1.0
     * 
     * @deprecated Use a single instance and start/stop connection to ANT Radio
     * Service with initService()/releaseService(). 
     */
    @Deprecated
    public boolean destroy()
    {
        if(DEBUG)   Log.d(TAG, "destroy");

        releaseService();

        return true;
    }


    ////-------------------------------------------------

    /**
     * Enable.
     *
     * @throws AntInterfaceException
     * @throws AntServiceNotConnectedException
     * @throws AntRemoteException
     * @since 1.0
     */
    public void enable() throws AntInterfaceException
    {
        if(!mServiceConnected)
        {
            throw new AntServiceNotConnectedException();
        }

        try
        {
            if(!mAntReceiver.enable())
            {
                throw new AntInterfaceException();
            }
        }
        catch(RemoteException e)
        {
            throw new AntRemoteException(e);
        }
    }

    /**
     * Disable.
     *
     * @throws AntInterfaceException
     * @throws AntServiceNotConnectedException
     * @throws AntRemoteException
     * @since 1.0
     */
    public void disable() throws AntInterfaceException
    {
        if(!mServiceConnected)
        {
            throw new AntServiceNotConnectedException();
        }

        try
        {
            if(!mAntReceiver.disable())
            {
                throw new AntInterfaceException();
            }
        }
        catch(RemoteException e)
        {
            throw new AntRemoteException(e);
        }
    }

    /**
     * Checks if is enabled.
     *
     * @return true, if is enabled.
     * @throws AntInterfaceException
     * @throws AntServiceNotConnectedException
     * @throws AntRemoteException
     * @since 1.0
     */
    public boolean isEnabled() throws AntInterfaceException
    {
        if(!mServiceConnected)
        {
            throw new AntServiceNotConnectedException();
        }

        try
        {
            return mAntReceiver.isEnabled();
        }
        catch(RemoteException e)
        {
            throw new AntRemoteException(e);
        }
    }

    /**
     * ANT tx message.
     *
     * @param message the message
     * @throws AntInterfaceException
     * @throws AntServiceNotConnectedException
     * @throws AntRemoteException
     * @since 1.0
     */
    public void ANTTxMessage(byte[] message) throws AntInterfaceException
    {
        if(DEBUG) Log.d(TAG, "ANTTxMessage");

        if(!mServiceConnected)
        {
            throw new AntServiceNotConnectedException();
        }

        try
        {
            if(!mAntReceiver.ANTTxMessage(message))
            {
                throw new AntInterfaceException();
            }
        }
        catch(RemoteException e)
        {
            throw new AntRemoteException(e);
        }
    }

    /**
     * ANT reset system.
     *
     * @throws AntInterfaceException
     * @throws AntServiceNotConnectedException
     * @throws AntRemoteException
     * @since 1.0
     */
    public void ANTResetSystem() throws AntInterfaceException
    {
        if(!mServiceConnected)
        {
            throw new AntServiceNotConnectedException();
        }

        try
        {
            if(!mAntReceiver.ANTResetSystem())
            {
                throw new AntInterfaceException();
            }
        }
        catch(RemoteException e)
        {
            throw new AntRemoteException(e);
        }
    }

    /**
     * ANT unassign channel.
     *
     * @param channelNumber the channel number
     * @throws AntInterfaceException
     * @throws AntServiceNotConnectedException
     * @throws AntRemoteException
     * @since 1.0
     */
    public void ANTUnassignChannel(byte channelNumber) throws AntInterfaceException
    {
        if(!mServiceConnected)
        {
            throw new AntServiceNotConnectedException();
        }

        try
        {
            if(!mAntReceiver.ANTUnassignChannel(channelNumber))
            {
                throw new AntInterfaceException();
            }
        }
        catch(RemoteException e)
        {
            throw new AntRemoteException(e);
        }
    }

    /**
     * ANT assign channel.
     *
     * @param channelNumber the channel number
     * @param channelType the channel type
     * @param networkNumber the network number
     * @throws AntInterfaceException
     * @throws AntServiceNotConnectedException
     * @throws AntRemoteException
     * @since 1.0
     */
    public void ANTAssignChannel(byte channelNumber, byte channelType, byte networkNumber) throws AntInterfaceException
    {
        if(!mServiceConnected)
        {
            throw new AntServiceNotConnectedException();
        }

        try
        {
            if(!mAntReceiver.ANTAssignChannel(channelNumber, channelType, networkNumber))
            {
                throw new AntInterfaceException();
            }
        }
        catch(RemoteException e)
        {
            throw new AntRemoteException(e);
        }
    }

    /**
     * ANT set channel id.
     *
     * @param channelNumber the channel number
     * @param deviceNumber the device number
     * @param deviceType the device type
     * @param txType the tx type
     * @throws AntInterfaceException
     * @throws AntServiceNotConnectedException
     * @throws AntRemoteException
     * @since 1.0
     */
    public void ANTSetChannelId(byte channelNumber, short deviceNumber, byte deviceType, byte txType) throws AntInterfaceException
    {
        if(!mServiceConnected)
        {
            throw new AntServiceNotConnectedException();
        }

        try
        {
            if(!mAntReceiver.ANTSetChannelId(channelNumber, deviceNumber, deviceType, txType))
            {
                throw new AntInterfaceException();
            }
        }
        catch(RemoteException e)
        {
            throw new AntRemoteException(e);
        }
    }

    /**
     * ANT set channel period.
     *
     * @param channelNumber the channel number
     * @param channelPeriod the channel period
     * @throws AntInterfaceException
     * @throws AntServiceNotConnectedException
     * @throws AntRemoteException
     * @since 1.0
     */
    public void ANTSetChannelPeriod(byte channelNumber, short channelPeriod) throws AntInterfaceException
    {
        if(!mServiceConnected)
        {
            throw new AntServiceNotConnectedException();
        }

        try
        {
            if(!mAntReceiver.ANTSetChannelPeriod(channelNumber, channelPeriod))
            {
                throw new AntInterfaceException();
            }
        }
        catch(RemoteException e)
        {
            throw new AntRemoteException(e);
        }
    }

    /**
     * ANT set channel rf freq.
     *
     * @param channelNumber the channel number
     * @param radioFrequency the radio frequency
     * @throws AntInterfaceException
     * @throws AntServiceNotConnectedException
     * @throws AntRemoteException
     * @since 1.0
     */
    public void ANTSetChannelRFFreq(byte channelNumber, byte radioFrequency) throws AntInterfaceException
    {
        if(!mServiceConnected)
        {
            throw new AntServiceNotConnectedException();
        }

        try
        {
            if(!mAntReceiver.ANTSetChannelRFFreq(channelNumber, radioFrequency))
            {
                throw new AntInterfaceException();
            }
        }
        catch(RemoteException e)
        {
            throw new AntRemoteException(e);
        }
    }

    /**
     * ANT set channel search timeout.
     *
     * @param channelNumber the channel number
     * @param searchTimeout the search timeout
     * @throws AntInterfaceException
     * @throws AntServiceNotConnectedException
     * @throws AntRemoteException
     * @since 1.0
     */
    public void ANTSetChannelSearchTimeout(byte channelNumber, byte searchTimeout) throws AntInterfaceException
    {
        if(!mServiceConnected)
        {
            throw new AntServiceNotConnectedException();
        }

        try
        {
            if(!mAntReceiver.ANTSetChannelSearchTimeout(channelNumber, searchTimeout))
            {
                throw new AntInterfaceException();
            }
        }
        catch(RemoteException e)
        {
            throw new AntRemoteException(e);
        }
    }

    /**
     * ANT set low priority channel search timeout.
     *
     * @param channelNumber the channel number
     * @param searchTimeout the search timeout
     * @throws AntInterfaceException
     * @throws AntServiceNotConnectedException
     * @throws AntRemoteException
     * @since 1.0
     */
    public void ANTSetLowPriorityChannelSearchTimeout(byte channelNumber, byte searchTimeout) throws AntInterfaceException
    {
        if(!mServiceConnected)
        {
            throw new AntServiceNotConnectedException();
        }

        try
        {
            if(!mAntReceiver.ANTSetLowPriorityChannelSearchTimeout(channelNumber, searchTimeout))
            {
                throw new AntInterfaceException();
            }
        }
        catch(RemoteException e)
        {
            throw new AntRemoteException(e);
        }
    } 

    /**
     * ANT set proximity search.
     *
     * @param channelNumber the channel number
     * @param searchThreshold the search threshold
     * @throws AntInterfaceException
     * @throws AntServiceNotConnectedException
     * @throws AntRemoteException
     * @since 1.0
     */
    public void ANTSetProximitySearch(byte channelNumber, byte searchThreshold) throws AntInterfaceException
    {
        if(!mServiceConnected)
        {
            throw new AntServiceNotConnectedException();
        }

        try
        {
            if(!mAntReceiver.ANTSetProximitySearch(channelNumber, searchThreshold))
            {
                throw new AntInterfaceException();
            }
        }
        catch(RemoteException e)
        {
            throw new AntRemoteException(e);
        }
    }

    /**
     * ANT set channel transmit power
     * @param channelNumber the channel number
     * @param txPower the transmit power level
     * @throws AntInterfaceException
     * @throws AntServiceNotConnectedException
     * @throws AntRemoteException
     * @since 1.0
     */
    public void ANTSetChannelTxPower(byte channelNumber, byte txPower) throws AntInterfaceException
    {
        if(!mServiceConnected)
        {
            throw new AntServiceNotConnectedException();
        }

        try
        {
            if(!mAntReceiver.ANTSetChannelTxPower(channelNumber, txPower))
            {
                throw new AntInterfaceException();
            }
        }
        catch(RemoteException e)
        {
            throw new AntRemoteException(e);
        }
    }

    /**
     * ANT add channel id.
     *
     * @param channelNumber the channel number
     * @param deviceNumber the device number
     * @param deviceType the device type
     * @param txType the tx type
     * @param listIndex the list index
     * @throws AntInterfaceException
     * @throws AntServiceNotConnectedException
     * @throws AntRemoteException
     * @since 1.0
     */
    public void ANTAddChannelId(byte channelNumber, short deviceNumber, byte deviceType, byte txType, byte listIndex) throws AntInterfaceException
    {
        if(!mServiceConnected)
        {
            throw new AntServiceNotConnectedException();
        }

        try
        {
            if(!mAntReceiver.ANTAddChannelId(channelNumber, deviceNumber, deviceType, txType, listIndex))
            {
                throw new AntInterfaceException();
            }
        }
        catch(RemoteException e)
        {
            throw new AntRemoteException(e);
        }
    } 

    /**
     * ANT config list.
     *
     * @param channelNumber the channel number
     * @param listSize the list size
     * @param exclude the exclude
     * @throws AntInterfaceException
     * @throws AntServiceNotConnectedException
     * @throws AntRemoteException
     * @since 1.0
     */
    public void ANTConfigList(byte channelNumber, byte listSize, byte exclude) throws AntInterfaceException
    {
        if(!mServiceConnected)
        {
            throw new AntServiceNotConnectedException();
        }

        try
        {
            if(!mAntReceiver.ANTConfigList(channelNumber, listSize, exclude))
            {
                throw new AntInterfaceException();
            }
        }
        catch(RemoteException e)
        {
            throw new AntRemoteException(e);
        }
    }

    /**
     * ANT config event buffering.
     *
     * @param screenOnFlushTimerInterval the screen on flush timer interval
     * @param screenOnFlushBufferThreshold the screen on flush buffer threshold
     * @param screenOffFlushTimerInterval the screen off flush timer interval
     * @param screenOffFlushBufferThreshold the screen off flush buffer threshold
     * @throws AntInterfaceException
     * @throws AntServiceNotConnectedException
     * @throws AntRemoteException
     * @since 1.3
     */
    public void ANTConfigEventBuffering(short screenOnFlushTimerInterval, short screenOnFlushBufferThreshold, short screenOffFlushTimerInterval, short screenOffFlushBufferThreshold) throws AntInterfaceException
    {
        if(!mServiceConnected)
        {
            throw new AntServiceNotConnectedException();
        }

        try
        {
            if(!mAntReceiver.ANTConfigEventBuffering(screenOnFlushTimerInterval, screenOnFlushBufferThreshold, screenOffFlushTimerInterval, screenOffFlushBufferThreshold))
            {
                throw new AntInterfaceException();
            }
        }
        catch(RemoteException e)
        {
            throw new AntRemoteException(e);
        }
    }

    /**
     * ANT disable event buffering.
     *
     * @throws AntInterfaceException
     * @throws AntServiceNotConnectedException
     * @throws AntRemoteException
     * @since 1.1
     */
    public void ANTDisableEventBuffering() throws AntInterfaceException
    {
        if(!mServiceConnected)
        {
            throw new AntServiceNotConnectedException();
        }

        try
        {
            if(!mAntReceiver.ANTDisableEventBuffering())
            {
                throw new AntInterfaceException();
            }
        }
        catch(RemoteException e)
        {
            throw new AntRemoteException(e);
        }
    }

    /**
     * ANT open channel.
     *
     * @param channelNumber the channel number
     * @throws AntInterfaceException
     * @throws AntServiceNotConnectedException
     * @throws AntRemoteException
     * @since 1.0
     */
    public void ANTOpenChannel(byte channelNumber) throws AntInterfaceException
    {
        if(!mServiceConnected)
        {
            throw new AntServiceNotConnectedException();
        }

        try
        {
            if(!mAntReceiver.ANTOpenChannel(channelNumber))
            {
                throw new AntInterfaceException();
            }
        }
        catch(RemoteException e)
        {
            throw new AntRemoteException(e);
        }
    }

    /**
     * ANT close channel.
     *
     * @param channelNumber the channel number
     * @throws AntInterfaceException
     * @throws AntServiceNotConnectedException
     * @throws AntRemoteException
     * @since 1.0
     */
    public void ANTCloseChannel(byte channelNumber) throws AntInterfaceException
    {
        if(!mServiceConnected)
        {
            throw new AntServiceNotConnectedException();
        }

        try
        {
            if(!mAntReceiver.ANTCloseChannel(channelNumber))
            {
                throw new AntInterfaceException();
            }
        }
        catch(RemoteException e)
        {
            throw new AntRemoteException(e);
        }
    }

    /**
     * ANT request message.
     *
     * @param channelNumber the channel number
     * @param messageID the message id
     * @throws AntInterfaceException
     * @throws AntServiceNotConnectedException
     * @throws AntRemoteException
     * @since 1.0
     */
    public void ANTRequestMessage(byte channelNumber, byte messageID) throws AntInterfaceException
    {
        if(!mServiceConnected)
        {
            throw new AntServiceNotConnectedException();
        }

        try
        {
            if(!mAntReceiver.ANTRequestMessage(channelNumber, messageID))
            {
                throw new AntInterfaceException();
            }
        }
        catch(RemoteException e)
        {
            throw new AntRemoteException(e);
        }
    }

    /**
     * ANT send broadcast data.
     *
     * @param channelNumber the channel number
     * @param txBuffer the tx buffer
     * @throws AntInterfaceException
     * @throws AntServiceNotConnectedException
     * @throws AntRemoteException
     * @since 1.0
     */
    public void ANTSendBroadcastData(byte channelNumber, byte[] txBuffer) throws AntInterfaceException
    {
        if(!mServiceConnected)
        {
            throw new AntServiceNotConnectedException();
        }

        try
        {
            if(!mAntReceiver.ANTSendBroadcastData(channelNumber, txBuffer))
            {
                throw new AntInterfaceException();
            }
        }
        catch(RemoteException e)
        {
            throw new AntRemoteException(e);
        }
    }

    /**
     * ANT send acknowledged data.
     *
     * @param channelNumber the channel number
     * @param txBuffer the tx buffer
     * @throws AntInterfaceException
     * @throws AntServiceNotConnectedException
     * @throws AntRemoteException
     * @since 1.0
     */
    public void ANTSendAcknowledgedData(byte channelNumber, byte[] txBuffer) throws AntInterfaceException
    {
        if(!mServiceConnected)
        {
            throw new AntServiceNotConnectedException();
        }

        try
        {
            if(!mAntReceiver.ANTSendAcknowledgedData(channelNumber, txBuffer))
            {
                throw new AntInterfaceException();
            }
        }
        catch(RemoteException e)
        {
            throw new AntRemoteException(e);
        }
    } 

    /**
     * ANT send burst transfer packet.
     *
     * @param control the control
     * @param txBuffer the tx buffer
     * @throws AntInterfaceException
     * @throws AntServiceNotConnectedException
     * @throws AntRemoteException
     * @since 1.0
     */
    public void ANTSendBurstTransferPacket(byte control, byte[] txBuffer) throws AntInterfaceException
    {
        if(!mServiceConnected)
        {
            throw new AntServiceNotConnectedException();
        }

        try
        {
            if(!mAntReceiver.ANTSendBurstTransferPacket(control, txBuffer))
            {
                throw new AntInterfaceException();
            }
        }
        catch(RemoteException e)
        {
            throw new AntRemoteException(e);
        }
    } 

    /**
     * Transmits the given data on channelNumber as a burst message.
     * 
     * @param channelNumber Which channel to transmit on.
     * @param txBuffer The data to send.
     * @return The number of bytes still to be sent (approximately).  0 if success.
     * @throws AntInterfaceException
     * @throws AntServiceNotConnectedException
     * @throws AntRemoteException
     */
    public int ANTSendBurstTransfer(byte channelNumber, byte[] txBuffer) throws AntInterfaceException
    {
        if(!mServiceConnected)
        {
            throw new AntServiceNotConnectedException();
        }

        try
        {
            return mAntReceiver.ANTSendBurstTransfer(channelNumber, txBuffer);
        }
        catch(RemoteException e)
        {
            throw new AntRemoteException(e);
        }
    }

    /**
     * ANT send partial burst.
     *
     * @param channelNumber the channel number
     * @param txBuffer the tx buffer
     * @param initialPacket the initial packet
     * @param containsEndOfBurst the contains end of burst
     * @return The number of bytes still to be sent (approximately).  0 if success.
     * @throws AntInterfaceException
     * @throws AntServiceNotConnectedException
     * @throws AntRemoteException
     * @since 1.0
     */
    public int ANTSendPartialBurst(byte channelNumber, byte[] txBuffer, int initialPacket, boolean containsEndOfBurst) throws AntInterfaceException
    {
        if(!mServiceConnected)
        {
            throw new AntServiceNotConnectedException();
        }

        try
        {
            return mAntReceiver.ANTTransmitBurst(channelNumber, txBuffer, initialPacket, containsEndOfBurst);
        }
        catch(RemoteException e)
        {
            throw new AntRemoteException(e);
        }
    }

    /**
     * Returns the version code (eg. 1) of ANTLib used by the ANT Radio Service
     *
     * @return the service library version code, or 0 on error.
     * @throws AntInterfaceException
     * @throws AntServiceNotConnectedException
     * @throws AntRemoteException
     * @since 1.2
     */
    public int getServiceLibraryVersionCode()  throws AntInterfaceException
    {
        if(!mServiceConnected)
        {
            throw new AntServiceNotConnectedException();
        }

        if(mServiceLibraryVersionCode == 0)
        {
            try
            {
                mServiceLibraryVersionCode = mAntReceiver.getServiceLibraryVersionCode();
            }
            catch(RemoteException e)
            {
                throw new AntRemoteException(e);
            }
        }

        return mServiceLibraryVersionCode;
    }

    /**
     * Returns the version name (eg "1.0") of ANTLib used by the ANT Radio Service
     *
     * @return the service library version name, or null on error.
     * @throws AntInterfaceException
     * @throws AntServiceNotConnectedException
     * @throws AntRemoteException
     * @since 1.2
     */
    public String getServiceLibraryVersionName()  throws AntInterfaceException
    {
        if(!mServiceConnected)
        {
            throw new AntServiceNotConnectedException();
        }

        try
        {
            return mAntReceiver.getServiceLibraryVersionName();
        }
        catch(RemoteException e)
        {
            throw new AntRemoteException(e);
        }
    }

    /**
     * Take control of the ANT Radio.
     * 
     * @return True if control has been granted, false if another application has control or the request failed.
     * @throws AntInterfaceException
     * @throws AntServiceNotConnectedException
     * @throws AntRemoteException
     * @since 1.5
     */
    public boolean claimInterface() throws AntInterfaceException
    {
        if(!mServiceConnected)
        {
            throw new AntServiceNotConnectedException();
        }

        try
        {
            return mAntReceiver.claimInterface();
        }
        catch(RemoteException e)
        {
            throw new AntRemoteException(e);
        }
    }

    /**
     * Give up control of the ANT Radio.
     * 
     * @return True if control has been given up, false if this application did not have control.
     * @throws AntInterfaceException
     * @throws AntServiceNotConnectedException
     * @throws AntRemoteException
     * @since 1.5
     */
    public boolean releaseInterface() throws AntInterfaceException
    {
        if(!mServiceConnected)
        {
            throw new AntServiceNotConnectedException();
        }

        try
        {
            return mAntReceiver.releaseInterface();
        }
        catch(RemoteException e)
        {
            throw new AntRemoteException(e);
        }
    }

    /**
     * Claims the interface if it is available.  If not the user will be prompted (on the notification bar) if a force claim should be done.
     * If the ANT Interface is claimed, an AntInterfaceIntent.ANT_INTERFACE_CLAIMED_ACTION intent will be sent, with the current applications pid.
     * 
     * @param String appName The name if this application, to show to the user.
     * @returns false if a claim interface request notification already exists.
     * @throws IllegalArgumentException
     * @throws AntInterfaceException
     * @throws AntServiceNotConnectedException
     * @throws AntRemoteException
     * @since 1.5
     */
    public boolean requestForceClaimInterface(String appName) throws AntInterfaceException
    {
        if((null == appName) || (appName.equals("")))
        {
            throw new IllegalArgumentException("Application name not specified");
        }

        if(!mServiceConnected)
        {
            throw new AntServiceNotConnectedException();
        }

        try
        {
            return mAntReceiver.requestForceClaimInterface(appName);
        }
        catch(RemoteException e)
        {
            throw new AntRemoteException(e);
        }
    }
    
    /**
     * Clears the notification asking the user if they would like to seize control of the ANT Radio.
     * 
     * @returns false if this process is not requesting to claim the interface.
     * @throws AntInterfaceException
     * @throws AntServiceNotConnectedException
     * @throws AntRemoteException
     * @since 1.5
     */
    public boolean stopRequestForceClaimInterface() throws AntInterfaceException
    {
        if(!mServiceConnected)
        {
            throw new AntServiceNotConnectedException();
        }

        try
        {
            return mAntReceiver.stopRequestForceClaimInterface();
        }
        catch(RemoteException e)
        {
            throw new AntRemoteException(e);
        }
    }

    /**
     * Check if the calling application has control of the ANT Radio.
     * 
     * @return True if control is currently granted.
     * @throws AntInterfaceException
     * @throws AntServiceNotConnectedException
     * @throws AntRemoteException
     * @since 1.5
     */
    public boolean hasClaimedInterface() throws AntInterfaceException
    {
        if(!mServiceConnected)
        {
            throw new AntServiceNotConnectedException();
        }

        try
        {
            return mAntReceiver.hasClaimedInterface();
        }
        catch(RemoteException e)
        {
            throw new AntRemoteException(e);
        }
    }

    /**
     * Check if this device has support for ANT.
     * 
     * @return True if the device supports ANT (may still require ANT Radio Service be installed).
     * @since 1.5
     */
    public static boolean hasAntSupport(Context pContext)
    {
        synchronized(CHECK_ANT_SUPPORTED_LOCK)
        {
            if(!mCheckedAntSupported)
            {
                mAntSupported = Arrays.asList(pContext.getPackageManager().getSystemSharedLibraryNames()).contains(ANT_LIBRARY_NAME);
                mCheckedAntSupported = true;
            }

            return mAntSupported;
        }
    }
}
