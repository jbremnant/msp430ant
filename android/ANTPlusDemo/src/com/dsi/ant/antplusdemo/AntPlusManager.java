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
package com.dsi.ant.antplusdemo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.dsi.ant.exception.*;
import com.dsi.ant.AntInterface;
import com.dsi.ant.AntInterfaceIntent;
import com.dsi.ant.AntMesg;
import com.dsi.ant.AntDefine;

/**
 * This class handles connecting to the AntRadio service, setting up the channels,
 * and processing Ant events.
 */
public class AntPlusManager {
    
    /**
     * Defines the interface needed to work with all call backs this class makes
     */
    public interface Callbacks
    {
        public void errorCallback();
        public void notifyAntStateChanged();
        public void notifyChannelStateChanged(byte channel);
        public void notifyChannelDataChanged(byte channel);
    }
    
    /** The Log Tag. */
    public static final String TAG = "ANTApp";
    
    /** The key for referencing the interrupted variable in saved instance data. */
    static final String ANT_INTERRUPTED_KEY = "ant_interrupted";
    
    /** The key for referencing the state variable in saved instance data. */
    static final String ANT_STATE_KEY = "ant_state";
    
    /** The interface to the ANT radio. */
    private AntInterface mAntReceiver;
    
    /** Is the ANT background service connected. */
    private boolean mServiceConnected = false;
    
    /** Whether to process ANT data. */
    private boolean mAntProcessingEnabled;
    
    /** Stores which ANT status Intents to receive. */
    private IntentFilter statusIntentFilter;
    
    /** Flag to know if the ANT App was interrupted. */
    private boolean mAntInterrupted = false;
    
    /** Flag to know if an ANT Reset was triggered by this application. */
    private boolean mAntResetSent = false;
    
    /** Flag if waiting for ANT_ENABLED. Default to true as assume ANT is always enabled */
    private boolean mEnabling = true;
    
    // ANT Channels
    /** The ANT channel for the HRM. */
    static final byte HRM_CHANNEL = (byte) 0;
    
    /** ANT+ device type for an HRM */
    // static final byte HRM_DEVICE_TYPE = 0x78;
    static final byte HRM_DEVICE_TYPE = 0x00;
    
    /** ANT+ channel period for an HRM */
    static final short HRM_PERIOD = 8070;
    
    /** The ANT channel for the SDM. */
    static final byte SDM_CHANNEL = (byte) 1;
    
    /** ANT+ device type for an SDM */
    static final byte SDM_DEVICE_TYPE = 0x7C;
    
    /** ANT+ channel period for an SDM */
    static final short SDM_PERIOD = 8134;
    
    /** The ANT channel for the Weight Scale. */
    static final byte WEIGHT_CHANNEL = (byte) 2;
    
    /** ANT+ device type for a Weight scale */
    static final byte WEIGHT_DEVICE_TYPE = 0x77;
    
    /** ANT+ channel period for a Weight scale */
    static final short WEIGHT_PERIOD = 8192;
    
 // Variables to keep track of the status of each sensor
    /** Has the user profile been sent to the weight scale. */
    private boolean mHasSentProfile;
    
    /** The weight scale has calculated the weight. */
    private boolean mSessionDone;
    
    /** The current HRM page/toggle bit state. */
    private HRMStatePage mStateHRM = HRMStatePage.INIT;
    
    /** Has the starting distance been recorded. */
    private boolean mDistanceInit;
    
    /** Has the starting stride count been recorded. */
    private boolean mStridesInit;
    
    /** The distance. */
    private float mAccumDistance;
    
    /** The number of strides. */
    private long mAccumStrides;
    
    /** The m prev distance. */
    private float mPrevDistance;
    
    /** The m prev strides. */
    private int mPrevStrides;
    
    /** Description of ANT's current state */
    private String mAntStateText = "";
    
    /** Possible states of a device channel */
    public enum ChannelStates
    {
       /** Channel was explicitly closed or has not been opened */
       CLOSED,
       
       /** User has requested we open the channel, but we are waiting for a reset */
       PENDING_OPEN,
       
       /** Channel is opened, but we have not received any data yet */
       SEARCHING,
       
       /** Channel is opened and has received status data from the device most recently */
       TRACKING_STATUS,
       
       /** Channel is opened and has received measurement data most recently */
       TRACKING_DATA,
       
       /** Channel is closed as the result of a search timeout */
       OFFLINE
    }

    /** Current state of the HRM channel */
    private ChannelStates mHrmState = ChannelStates.CLOSED;

    /** Last measured BPM form the HRM device */
    private int mBPM = 0;
    private int mTest = 0;

    /** Current state of the SDM channel */
    private ChannelStates mSdmState = ChannelStates.CLOSED;
    
    /** Last measured cadence from the SDM */
    private float mCadence = 0f;

    /** Last measured speed from the SDM */
    private float mSpeed = 0f;

    /** Current state of the weight scale channel */
    private ChannelStates mWeightState = ChannelStates.CLOSED;
    
    /** Most recent status string for the weight scale */
    private String mWeightStatus = "";
    
    /** Most recent weight received */
    private int mWeight = 0;
    
    //Flags used for deferred opening of channels
    /** Flag indicating that opening of the HRM channel was deferred */
    private boolean mDeferredHrmStart = false;
    
    /** Flag indicating that opening of the SDM channel was deferred */
    private boolean mDeferredSdmStart = false;
    
    /** Flag indicating that opening of the weight scale channel was deferred */
    private boolean mDeferredWeightStart = false;
    
    /** HRM device number. */
    private short mDeviceNumberHRM;
    
    /** SDM device number. */
    private short mDeviceNumberSDM;
    
    /** Weight scale device number. */
    private short mDeviceNumberWGT;
    
    /** Devices must be within this bin to be found during (proximity) search. */
    private byte mProximityThreshold;
    
    //TODO You will want to set a separate threshold for screen off and (if desired) screen on.
    /** Data buffered for event buffering before flush. */
    private short mBufferThreshold;
    
    /** If this application has control of the ANT Interface. */
    private boolean mClaimedAntInterface;

    /**
     * The possible HRM page toggle bit states.
     */
    public enum HRMStatePage
    {
       /** Toggle bit is 0. */
       TOGGLE0,
       
       /** Toggle bit is 1. */
       TOGGLE1,
       
       /** Initialising (bit value not checked). */
       INIT,
       
       /** Extended pages are valid. */
       EXT
    }
    
    private Activity mActivity;
    
    private Callbacks mCallbackSink;
    
    /**
     * Default Constructor
     */
    public AntPlusManager(Activity activity, Bundle savedInstanceState, Callbacks callbacks)
    {
        Log.d(TAG, "AntChannelManager: enter Constructor");
        mActivity = activity;
        mCallbackSink = callbacks;
        
        //Set initial state values
        mDeferredHrmStart = false;
        mHrmState = ChannelStates.CLOSED;
        mDeferredSdmStart = false;
        mSdmState = ChannelStates.CLOSED;
        mDeferredWeightStart = false;
        mWeightState = ChannelStates.CLOSED;
        
        /*Retrieve the ANT state and find out whether the ANT App was interrupted*/
        if (savedInstanceState != null) 
        {
           Bundle antState = savedInstanceState.getBundle(ANT_STATE_KEY);
           if (antState != null) 
           {
               mAntInterrupted = antState.getBoolean(ANT_INTERRUPTED_KEY, false);       
           }
        }
        
        mClaimedAntInterface = false;
        
        // ANT intent broadcasts.
        statusIntentFilter = new IntentFilter();
        statusIntentFilter.addAction(AntInterfaceIntent.ANT_ENABLED_ACTION);
        statusIntentFilter.addAction(AntInterfaceIntent.ANT_DISABLED_ACTION);
        statusIntentFilter.addAction(AntInterfaceIntent.ANT_RESET_ACTION);
        statusIntentFilter.addAction(AntInterfaceIntent.ANT_INTERFACE_CLAIMED_ACTION);
        
        mAntReceiver = new AntInterface();
    }
    
    
    /**
     * Creates the connection to the ANT service back-end.
     */
    public boolean start()
    {
        boolean initialised = false;
        
        if(AntInterface.hasAntSupport(mActivity))
        {
            if(!mAntReceiver.initService(mActivity.getApplicationContext(), mAntServiceListener))
            {
                // Need the ANT Radio Service installed.
                Log.e(TAG, "AntChannelManager Constructor: No ANT Service.");
                requestServiceinstall();
            }
            else
            {
                mServiceConnected = mAntReceiver.isServiceConnected();

                mActivity.getApplicationContext().registerReceiver(mAntStatusReceiver, statusIntentFilter);

                if(mServiceConnected)
                {
                    try
                    {
                        mClaimedAntInterface = mAntReceiver.hasClaimedInterface();
                        if(mClaimedAntInterface)
                        {
                            receiveAntRxMessages(true);
                        }
                    }
                    catch (AntInterfaceException e)
                    {
                        antError();
                    }
                }
                
                initialised = true;
            }
        }
        
        return initialised;
    }
    
    public void resetCallbacks(Activity activity, Callbacks callbacks)
    {
        mActivity = activity;
        mCallbackSink = callbacks;
    }
    
    /**
     * Requests that the user install the needed service for ant
     */
    void requestServiceinstall()
    {
        Toast installNotification = Toast.makeText(mActivity, mActivity.getResources().getString(R.string.Notify_Service_Required), Toast.LENGTH_LONG);
        installNotification.show();

        AntInterface.goToMarket(mActivity);
        
        mActivity.finish();
    }
    
    //Getters and setters
    public AntInterface getAntReceiver()
    {
        return mAntReceiver;
    }
    
    public boolean isServiceConnected()
    {
        return mServiceConnected;
    }

    public short getDeviceNumberHRM()
    {
        return mDeviceNumberHRM;
    }

    public void setDeviceNumberHRM(short deviceNumberHRM)
    {
        this.mDeviceNumberHRM = deviceNumberHRM;
    }

    public short getDeviceNumberSDM()
    {
        return mDeviceNumberSDM;
    }

    public void setDeviceNumberSDM(short deviceNumberSDM)
    {
        this.mDeviceNumberSDM = deviceNumberSDM;
    }

    public short getDeviceNumberWGT()
    {
        return mDeviceNumberWGT;
    }

    public void setDeviceNumberWGT(short deviceNumberWGT)
    {
        this.mDeviceNumberWGT = deviceNumberWGT;
    }
    
    public byte getProximityThreshold()
    {
        return mProximityThreshold;
    }

    public void setProximityThreshold(byte proximityThreshold)
    {
        this.mProximityThreshold = proximityThreshold;
    }

    public short getBufferThreshold()
    {
        return mBufferThreshold;
    }

    public void setBufferThreshold(short bufferThreshold)
    {
        this.mBufferThreshold = bufferThreshold;
    }
    
    public void pauseMessageProcessing()
    {
        mAntProcessingEnabled = false;
        mCallbackSink.notifyAntStateChanged();
    }
    
    public void resumeMessageProcessing()
    {
        mAntProcessingEnabled = true;
        mCallbackSink.notifyAntStateChanged();
    }
    
    public HRMStatePage getStateHRM()
    {
        return mStateHRM;
    }

    public float getAccumDistance()
    {
        return mAccumDistance;
    }

    public long getAccumStrides()
    {
        return mAccumStrides;
    }

    public ChannelStates getHrmState()
    {
        return mHrmState;
    }

    public int getBPM()
    {
        return mBPM;
    }
    public int getTest()
    {
    	return mTest;
    }

    public ChannelStates getSdmState()
    {
        return mSdmState;
    }

    public float getCadence()
    {
        return mCadence;
    }

    public float getSpeed()
    {
        return mSpeed;
    }

    public ChannelStates getWeightState()
    {
        return mWeightState;
    }

    public String getWeightStatus()
    {
        return mWeightStatus;
    }

    public int getWeight()
    {
        return mWeight;
    }

    public String getAntStateText()
    {
        return mAntStateText;
    }

    /** Save internal state to a bundle */
    public void saveState(Bundle bundle)
    {
        // save the ANT state into bundle for the activity restart
        mAntInterrupted = true ;
        Bundle antState = new Bundle();
        antState.putBoolean(ANT_INTERRUPTED_KEY, mAntInterrupted);
        antState.putBundle(ANT_STATE_KEY, antState);
        
        // save the state of current sensor connections
        bundle.putInt("HRM_STATE", mHrmState.ordinal());
        bundle.putInt("SDM_STATE", mSdmState.ordinal());
        bundle.putInt("WEIGHT_STATE", mWeightState.ordinal());
        
        bundle.putBoolean("HasSentProfile", mHasSentProfile);
        bundle.putBoolean("SessionDone", mSessionDone);
        bundle.putInt("BPM", mBPM);
        bundle.putFloat("Cadence", mCadence);
        bundle.putFloat("Speed", mSpeed);
        bundle.putString("WeightStatus", mWeightStatus);
        bundle.putInt("Weight", mWeight);
        
        // Save cumulative values
        bundle.putBoolean("DistanceInit", mDistanceInit);
        bundle.putBoolean("StridesInit", mStridesInit);
        bundle.putFloat("AccumDistance", mAccumDistance);
        bundle.putLong("AccumStrides", mAccumStrides);
        bundle.putFloat("PrevDistance", mPrevDistance);
        bundle.putInt("PrevStrides", mPrevStrides);
    }
    
    /** Load internal state from a bundle */
    public void loadState(Bundle bundle)
    {
     // Get stored state of ANT channels and measurements (used mostly in the case of a screen orientation change)
        mHrmState = ChannelStates.values()[bundle.getInt("HRM_STATE", ChannelStates.CLOSED.ordinal())];
        mSdmState = ChannelStates.values()[bundle.getInt("SDM_STATE", ChannelStates.CLOSED.ordinal())];
        mWeightState = ChannelStates.values()[bundle.getInt("WEIGHT_STATE", ChannelStates.CLOSED.ordinal())];

        mHasSentProfile = bundle.getBoolean("HasSentProfile");
        mSessionDone = bundle.getBoolean("SessionDone");
        mBPM = bundle.getInt("BPM");
        mCadence = bundle.getFloat("Cadence");
        mSpeed = bundle.getFloat("Speed");
        mWeightStatus = bundle.getString("WeightStatus");
        mWeight = bundle.getInt("Weight");
        if(mWeightStatus == null)
            mWeightStatus = "";

        // Initialise cumulative values
        mDistanceInit = bundle.getBoolean("DistanceInit");
        mStridesInit = bundle.getBoolean("StridesInit");
        mAccumDistance = bundle.getFloat("AccumDistance");
        mAccumStrides = bundle.getLong("AccumStrides");
        mPrevDistance = bundle.getFloat("PrevDistance");
        mPrevStrides = bundle.getInt("PrevStrides");
        
        //In case we missed a pending reset
        if(mHrmState == ChannelStates.PENDING_OPEN)
        {
            mHrmState = ChannelStates.CLOSED;
            mDeferredHrmStart = false;
        }
        if(mSdmState == ChannelStates.PENDING_OPEN)
        {
            mSdmState = ChannelStates.CLOSED;
            mDeferredSdmStart = false;
        }
        if(mWeightState == ChannelStates.PENDING_OPEN)
        {
            mWeightState = ChannelStates.CLOSED;
            mDeferredWeightStart = false;
        }
        
        //Update the display to reflect the loaded values
        mCallbackSink.notifyChannelStateChanged(HRM_CHANNEL);
        mCallbackSink.notifyChannelStateChanged(SDM_CHANNEL);
        mCallbackSink.notifyChannelStateChanged(WEIGHT_CHANNEL);
        mCallbackSink.notifyChannelDataChanged(HRM_CHANNEL);
        mCallbackSink.notifyChannelDataChanged(SDM_CHANNEL);
        mCallbackSink.notifyChannelDataChanged(WEIGHT_CHANNEL);
    }
    
    /**
     * Checks if ANT can be used by this application
     * Sets the AntState string to reflect current status.
     * @return true if this application can use the ANT chip, false otherwise.
     */
    public boolean checkAntState()
    {
        try
        {
            if(!AntInterface.hasAntSupport(mActivity.getApplicationContext()))
            {
                Log.w(TAG, "updateDisplay: ANT not supported");

                mAntStateText = mActivity.getString(R.string.Text_ANT_Not_Supported);
                return false;
            }
            else if(mServiceConnected && mAntReceiver.isEnabled())
            {
                if(mAntReceiver.hasClaimedInterface() || mAntReceiver.claimInterface())
                {
                    return true;
                }
                else
                {
                    mAntStateText = mActivity.getString(R.string.Text_ANT_In_Use);
                    return false;
                }
            }
            else if(mEnabling)
            {
                mAntStateText = mActivity.getString(R.string.Text_Enabling);
                return false;
            }
            else
            {
                Log.w(TAG, "updateDisplay: Service not connected/enabled");

                mAntStateText = mActivity.getString(R.string.Text_Disabled);
                return false;
            }
        }
        catch(AntInterfaceException e)
        {
            antError();
            return false;
        }
    }

    /**
     * Attempts to claim the Ant interface
     */
    public void tryClaimAnt()
    {
        try
        {
            mAntReceiver.requestForceClaimInterface(mActivity.getResources().getString(R.string.app_name));
        }
        catch(AntInterfaceException e)
        {
            antError();
        }
    }

    /**
     * Unregisters all our receivers in preparation for application shutdown
     */
    public void shutDown()
    {
        try
        {
            mActivity.getApplicationContext().unregisterReceiver(mAntStatusReceiver);
        }
        catch(IllegalArgumentException e)
        {
            // Receiver wasn't registered, ignore as that's what we wanted anyway
        }
        
        receiveAntRxMessages(false);
        
        if(mServiceConnected)
        {
            try
            {
                if(mClaimedAntInterface)
                {
                    Log.d(TAG, "AntChannelManager.shutDown: Releasing interface");

                    mAntReceiver.releaseInterface();
                }

                mAntReceiver.stopRequestForceClaimInterface();
            }
            catch(AntServiceNotConnectedException e)
            {
                // Ignore as we are disconnecting the service/closing the app anyway
            }
            catch(AntInterfaceException e)
            {
               Log.w(TAG, "Exception in AntChannelManager.shutDown", e);
            }
        }
    }

    /**
     * Class for receiving notifications about ANT service state.
     */
    private AntInterface.ServiceListener mAntServiceListener = new AntInterface.ServiceListener()
    {
        public void onServiceConnected()
        {
            Log.d(TAG, "mAntServiceListener onServiceConnected()");

            mServiceConnected = true;

            try
            {
                if (mEnabling)
                {
                    mAntReceiver.enable();
                }

                mClaimedAntInterface = mAntReceiver.hasClaimedInterface();

                if (mClaimedAntInterface)
                {
                    // mAntMessageReceiver should be registered any time we have
                    // control of the ANT Interface
                    receiveAntRxMessages(true);
                } else
                {
                    // Need to claim the ANT Interface if it is available, now
                    // the service is connected
                    if (mAntInterrupted == false)
                    {
                        mClaimedAntInterface = mAntReceiver.claimInterface();
                    } else
                    {
                        Log.i(TAG, "Not attempting to claim the ANT Interface as application was interrupted (leaving in previous state).");
                    }
                }
            } catch (AntInterfaceException e)
            {
                antError();
            }

            Log.d(TAG, "mAntServiceListener Displaying icons only if radio enabled");
            mCallbackSink.notifyAntStateChanged();
        }

        public void onServiceDisconnected()
        {
            Log.d(TAG, "mAntServiceListener onServiceDisconnected()");

            mServiceConnected = false;
            mEnabling = false;

            if (mClaimedAntInterface)
            {
                receiveAntRxMessages(false);
            }

            mCallbackSink.notifyAntStateChanged();
        }
    };
    
    /**
     * Configure the ANT radio to the user settings.
     */
    void setAntConfiguration()
    {
        if(mServiceConnected)
        {
            try
            {
                // Event Buffering Configuration
                if(mBufferThreshold > 0)
                {
                    //TODO For easy demonstration will set screen on and screen off thresholds to the same value.
                    // No buffering by interval here.
                    mAntReceiver.ANTConfigEventBuffering((short)0xFFFF, mBufferThreshold, (short)0xFFFF, mBufferThreshold);
                }
                else
                {
                    mAntReceiver.ANTDisableEventBuffering();
                }
            }
            catch(AntInterfaceException e)
            {
                Log.e(TAG, "Could not configure event buffering", e);
            }
        }
    }
    
    /**
     * Display to user that an error has occured communicating with ANT Radio.
     */
    private void antError()
    {
        mAntStateText = mActivity.getString(R.string.Text_ANT_Error);
        mCallbackSink.errorCallback();
    }
    
    /**
     * Opens a given channel using the proper configuration for the channel's sensor type.
     * @param channel The channel to Open.
     * @param deferToNextReset If true, channel will not open until the next reset.
     */
    public void openChannel(byte channel, boolean deferToNextReset)
    {
        if (!deferToNextReset)
        {
            short deviceNumber = 0;
            byte deviceType = 0;
            byte TransmissionType = 0; // Set to 0 for wild card search
            short period = 0;
            byte freq = 57; // 2457Mhz (ANT+ frequency)
            /*
            if(!antChannelSetup((byte) 0x01,        // Network:              1 (ANT+)
                    HRM_CHANNEL,
                    mDeviceNumberHRM,
                    (byte) 0x78,        // Device Type:          120 (HRM)
                    (byte) 0x00,        // Transmission Type:    wild card
                    (short) 0x1F86,     // Channel period:       8070 (~4Hz)
                    (byte) 0x39,        // RF Frequency:         57 (ANT+)
                    mProximityThreshold))
    {
        Log.w(TAG, "onClick Open channel: Channel Setup failed");
    }
            */
            switch (channel)
            {
                case HRM_CHANNEL:
                    deviceNumber = mDeviceNumberHRM;
                    deviceType = HRM_DEVICE_TYPE;
                    period = HRM_PERIOD;
                    mHrmState = ChannelStates.SEARCHING;
                    break;
                case SDM_CHANNEL:
                    deviceNumber = mDeviceNumberSDM;
                    deviceType = SDM_DEVICE_TYPE;
                    period = SDM_PERIOD;
                    mSdmState = ChannelStates.SEARCHING;

                    mDistanceInit = false;
                    mStridesInit = false;
                    mAccumDistance = 0;
                    mAccumStrides = 0;
                    mPrevDistance = 0;
                    mPrevStrides = 0;
                    break;
                case WEIGHT_CHANNEL:
                    deviceNumber = mDeviceNumberWGT;
                    deviceType = WEIGHT_DEVICE_TYPE;
                    period = WEIGHT_PERIOD;
                    mWeightState = ChannelStates.SEARCHING;

                    // Reset session
                    mHasSentProfile = false;
                    mSessionDone = false;
                    break;
            }

            mCallbackSink.notifyChannelStateChanged(channel);
            // Configure and open channel
            if (!antChannelSetup(
                    // (byte) 0x01, // Network: 1 (ANT+)
                    (byte) 0x00, // Network: 0 (JB)
                    channel, deviceNumber, deviceType, TransmissionType, period, freq,
                    mProximityThreshold))
            {
                Log.w(TAG, "openChannel: failed to configure and open channel " + channel + ".");
                switch(channel)
                {
                    case HRM_CHANNEL:
                        mHrmState = ChannelStates.CLOSED;
                        break;
                    case SDM_CHANNEL:
                        mSdmState = ChannelStates.CLOSED;
                        break;
                    case WEIGHT_CHANNEL:
                        mWeightState = ChannelStates.CLOSED;
                        break;
                }
                mCallbackSink.notifyChannelStateChanged(channel);
            }
        }
        else
        {
            switch(channel)
            {
                case HRM_CHANNEL:
                    mDeferredHrmStart = true;
                    mHrmState = ChannelStates.PENDING_OPEN;
                    break;
                case SDM_CHANNEL:
                    mDeferredSdmStart = true;
                    mSdmState = ChannelStates.PENDING_OPEN;
                    break;
                case WEIGHT_CHANNEL:
                    mDeferredWeightStart = true;
                    mWeightState = ChannelStates.PENDING_OPEN;
                    break;
            }
        }
    }
    
    /**
     * Attempts to cleanly close a specified channel 
     * @param channel The channel to close.
     */
    public void closeChannel(byte channel)
    {
        switch(channel)
        {
            case HRM_CHANNEL:
                mHrmState = ChannelStates.CLOSED;
                break;
            case SDM_CHANNEL:
                mSdmState = ChannelStates.CLOSED;
                break;
            case WEIGHT_CHANNEL:
                mWeightState = ChannelStates.CLOSED;
                break;
        }
        mCallbackSink.notifyChannelStateChanged(channel);
        try
        {
           mAntReceiver.ANTCloseChannel(channel);
           mAntReceiver.ANTUnassignChannel(channel);
        } catch (AntInterfaceException e)
        {
           Log.w(TAG, "closeChannel: could not cleanly close channel " + channel + ".");
           antError();
        }
    }
    
    /**
     * Resets the channel state machines, used in error recovery.
     */
    public void clearChannelStates()
    {
        mHrmState = ChannelStates.CLOSED;
        mCallbackSink.notifyChannelStateChanged(HRM_CHANNEL);
        mSdmState = ChannelStates.CLOSED;
        mCallbackSink.notifyChannelStateChanged(SDM_CHANNEL);
        mWeightState = ChannelStates.CLOSED;
        mCallbackSink.notifyChannelStateChanged(WEIGHT_CHANNEL);
    }
    
    /** check to see if a channel is open */
    public boolean isChannelOpen(byte channel)
    {
        switch(channel)
        {
            case HRM_CHANNEL:
                if(mHrmState == ChannelStates.CLOSED || mHrmState == ChannelStates.OFFLINE)
                    return false;
                break;
            case SDM_CHANNEL:
                if(mSdmState == ChannelStates.CLOSED || mSdmState == ChannelStates.OFFLINE)
                    return false;
                break;
            case WEIGHT_CHANNEL:
                if(mWeightState == ChannelStates.CLOSED || mWeightState == ChannelStates.OFFLINE)
                    return false;
                break;
            default:
                return false;
        }
        return true;
    }
    
    /** request an ANT reset */
    public void requestReset()
    {
        try
        {
            mAntResetSent = true;
            mAntReceiver.ANTResetSystem();
            setAntConfiguration();
        } catch (AntInterfaceException e) {
            Log.e(TAG, "requestReset: Could not reset ANT", e);
            mAntResetSent = false;
            //Cancel pending channel open requests
            if(mDeferredHrmStart)
            {
                mDeferredHrmStart = false;
                mHrmState = ChannelStates.CLOSED;
                mCallbackSink.notifyChannelStateChanged(HRM_CHANNEL);
            }
            if(mDeferredSdmStart)
            {
                mDeferredSdmStart = false;
                mSdmState = ChannelStates.CLOSED;
                mCallbackSink.notifyChannelStateChanged(SDM_CHANNEL);
            }
            if(mDeferredWeightStart)
            {
                mDeferredWeightStart = false;
                mWeightState = ChannelStates.CLOSED;
                mCallbackSink.notifyChannelStateChanged(WEIGHT_CHANNEL);
            }
        }
    }
    
    /** Receives all of the ANT status intents. */
    private final BroadcastReceiver mAntStatusReceiver = new BroadcastReceiver() 
    {      
       public void onReceive(Context context, Intent intent) 
       {
          String ANTAction = intent.getAction();

          Log.d(TAG, "enter onReceive: " + ANTAction);
          if (ANTAction.equals(AntInterfaceIntent.ANT_ENABLED_ACTION)) 
          {
             Log.i(TAG, "onReceive: ANT ENABLED");
             
             mEnabling = false;
             mCallbackSink.notifyAntStateChanged();
          }
          else if (ANTAction.equals(AntInterfaceIntent.ANT_DISABLED_ACTION)) 
          {
             Log.i(TAG, "onReceive: ANT DISABLED");
             
             mHrmState = ChannelStates.CLOSED;
             mCallbackSink.notifyChannelStateChanged(HRM_CHANNEL);
             mSdmState = ChannelStates.CLOSED;
             mCallbackSink.notifyChannelStateChanged(SDM_CHANNEL);
             mSdmState = ChannelStates.CLOSED;
             mCallbackSink.notifyChannelStateChanged(WEIGHT_CHANNEL);
             mAntStateText = mActivity.getString(R.string.Text_Disabled);
             
             mEnabling = false;
             
             mCallbackSink.notifyAntStateChanged();
          }
          else if (ANTAction.equals(AntInterfaceIntent.ANT_RESET_ACTION))
          {
             Log.d(TAG, "onReceive: ANT RESET");
             
             if(false == mAntResetSent)
             {
                //Someone else triggered an ANT reset
                Log.d(TAG, "onReceive: ANT RESET: Resetting state");
                
                if(mHrmState != ChannelStates.CLOSED)
                {
                   mHrmState = ChannelStates.CLOSED;
                   mCallbackSink.notifyChannelStateChanged(HRM_CHANNEL);
                }
                
                if(mSdmState != ChannelStates.CLOSED)
                {
                   mSdmState = ChannelStates.CLOSED;
                   mCallbackSink.notifyChannelStateChanged(SDM_CHANNEL);
                }
                
                if(mWeightState != ChannelStates.CLOSED)
                {
                   mWeightState = ChannelStates.CLOSED;
                   mCallbackSink.notifyChannelStateChanged(WEIGHT_CHANNEL);
                }
                
                mBufferThreshold = 0;
             }
             else
             {
                mAntResetSent = false;
                //Reconfigure event buffering
                setAntConfiguration();
                //Check if opening a channel was deferred, if so open it now.
                if(mDeferredHrmStart)
                {
                    openChannel(HRM_CHANNEL, false);
                    mDeferredHrmStart = false;
                }
                if(mDeferredSdmStart)
                {
                    openChannel(SDM_CHANNEL, false);
                    mDeferredSdmStart = false;
                }
                if(mDeferredWeightStart)
                {
                    openChannel(WEIGHT_CHANNEL, false);
                    mDeferredWeightStart = false;
                }
             }
          }
          else if (ANTAction.equals(AntInterfaceIntent.ANT_INTERFACE_CLAIMED_ACTION)) 
          {
             Log.i(TAG, "onReceive: ANT INTERFACE CLAIMED");
             
             boolean wasClaimed = mClaimedAntInterface;
             
             // Could also read ANT_INTERFACE_CLAIMED_PID from intent and see if it matches the current process PID.
             try
             {
                 mClaimedAntInterface = mAntReceiver.hasClaimedInterface();

                 if(mClaimedAntInterface)
                 {
                     Log.i(TAG, "onReceive: ANT Interface claimed");

                     receiveAntRxMessages(true);
                 }
                 else
                 {
                     // Another application claimed the ANT Interface...
                     if(wasClaimed)
                     {
                         // ...and we had control before that.  
                         Log.i(TAG, "onReceive: ANT Interface released");

                         receiveAntRxMessages(false);
                         
                         mAntStateText = mActivity.getString(R.string.Text_ANT_In_Use);
                         mCallbackSink.notifyAntStateChanged();
                     }
                 }
             }
             catch(AntInterfaceException e)
             {
                 antError();
             }
          }
       }
    };
    
    public static String getHexString(byte[] data)
    {
        if(null == data)
        {
            return "";
        }

        StringBuffer hexString = new StringBuffer();
        for(int i = 0;i < data.length; i++)
        {
           hexString.append("[").append(String.format("%02X", data[i] & 0xFF)).append("]");
        }

        return hexString.toString();
    }
    
    /** Receives all of the ANT message intents and dispatches to the proper handler. */
    private final BroadcastReceiver mAntMessageReceiver = new BroadcastReceiver() 
    {      
       Context mContext;

       public void onReceive(Context context, Intent intent) 
       {
          mContext = context;
          String ANTAction = intent.getAction();

          Log.d(TAG, "enter onReceive: " + ANTAction);
          if (ANTAction.equals(AntInterfaceIntent.ANT_RX_MESSAGE_ACTION)) 
          {
             Log.d(TAG, "onReceive: ANT RX MESSAGE");

             byte[] ANTRxMessage = intent.getByteArrayExtra(AntInterfaceIntent.ANT_MESSAGE);

             Log.d(TAG, "Rx:"+ getHexString(ANTRxMessage));

             switch(ANTRxMessage[AntMesg.MESG_ID_OFFSET])
             {
                 case AntMesg.MESG_STARTUP_MESG_ID:
                     break;
                 case AntMesg.MESG_BROADCAST_DATA_ID:
                 case AntMesg.MESG_ACKNOWLEDGED_DATA_ID:
                     byte channelNum = ANTRxMessage[AntMesg.MESG_DATA_OFFSET];
                     switch(channelNum)
                     {
                         case HRM_CHANNEL:
                             antDecodeHRM(ANTRxMessage);
                             antDecodeTest(ANTRxMessage);
                             break;
                         case SDM_CHANNEL:
                             antDecodeSDM(ANTRxMessage);
                             break;
                         case WEIGHT_CHANNEL:
                             antDecodeWeight(ANTRxMessage);
                             break;
                     }
                     break;
                 case AntMesg.MESG_BURST_DATA_ID:
                     break;
                 case AntMesg.MESG_RESPONSE_EVENT_ID:
                     responseEventHandler(ANTRxMessage);
                     break;
                 case AntMesg.MESG_CHANNEL_STATUS_ID:
                     break;
                 case AntMesg.MESG_CHANNEL_ID_ID:
                     short deviceNum = (short) ((ANTRxMessage[AntMesg.MESG_DATA_OFFSET + 1]&0xFF | ((ANTRxMessage[AntMesg.MESG_DATA_OFFSET + 2]&0xFF) << 8)) & 0xFFFF);
                     switch(ANTRxMessage[AntMesg.MESG_DATA_OFFSET]) //Switch on channel number
                     {
                         case HRM_CHANNEL:
                             Log.i(TAG, "onRecieve: Received HRM device number");
                             mDeviceNumberHRM = deviceNum;
                             break;
                         case SDM_CHANNEL:
                             Log.i(TAG, "onRecieve: Received SDM device number");
                             mDeviceNumberSDM = deviceNum;
                             break;
                         case WEIGHT_CHANNEL:
                             Log.i(TAG, "onRecieve: Received Weight device number");
                             mDeviceNumberWGT = deviceNum;
                             break;
                     }
                     break;
                 case AntMesg.MESG_VERSION_ID:
                     break;
                 case AntMesg.MESG_CAPABILITIES_ID:
                     break;
                 case AntMesg.MESG_GET_SERIAL_NUM_ID:
                     break;
                 case AntMesg.MESG_EXT_ACKNOWLEDGED_DATA_ID:
                     break;
                 case AntMesg.MESG_EXT_BROADCAST_DATA_ID:
                     break;
                 case AntMesg.MESG_EXT_BURST_DATA_ID:
                     break;
             }
          }
       }
       
       /**
        * Handles response and channel event messages
        * @param ANTRxMessage
        */
       private void responseEventHandler(byte[] ANTRxMessage)
       {
           // For a list of possible message codes
           // see ANT Message Protocol and Usage section 9.5.6.1
           // available from thisisant.com
           switch(ANTRxMessage[AntMesg.MESG_DATA_OFFSET + 2]) //Switch on message code
           {
               case AntDefine.EVENT_RX_SEARCH_TIMEOUT:
                   switch(ANTRxMessage[AntMesg.MESG_DATA_OFFSET]) //Switch on channel number
                   {
                       case HRM_CHANNEL:
                           try
                           {
                               Log.i(TAG, "responseEventHandler: Received search timeout on HRM channel");

                               mHrmState = ChannelStates.OFFLINE;
                               mCallbackSink.notifyChannelStateChanged(HRM_CHANNEL);
                               mAntReceiver.ANTUnassignChannel(HRM_CHANNEL);
                           }
                           catch(AntInterfaceException e)
                           {
                               antError();
                           }
                           break;
                       case SDM_CHANNEL:
                           try
                           {
                               Log.i(TAG, "responseEventHandler: Received search timeout on SDM channel");

                               mSdmState = ChannelStates.OFFLINE;
                               mCallbackSink.notifyChannelStateChanged(SDM_CHANNEL);
                               mAntReceiver.ANTUnassignChannel(SDM_CHANNEL);
                           }
                           catch(AntInterfaceException e)
                           {
                               antError();
                           }
                           break;
                       case WEIGHT_CHANNEL:
                           try
                           {
                               Log.i(TAG, "responseEventHandler: Received search timeout on weight channel");

                               mWeightState = ChannelStates.OFFLINE;
                               mCallbackSink.notifyChannelStateChanged(WEIGHT_CHANNEL);
                               mAntReceiver.ANTUnassignChannel(WEIGHT_CHANNEL);
                           }
                           catch(AntInterfaceException e)
                           {
                               antError();
                           }
                           break;
                   }
                   break;
           }
       }
       
       
       /**
        * Decode ANT+ Weight scale messages.
        *
        * @param ANTRxMessage the received ANT message.
        */
       private void antDecodeWeight(byte[] ANTRxMessage)
       {
          Log.d(TAG, "antDecodeWeight start");
          
          if(mAntProcessingEnabled)
          {
             Log.d(TAG, "antDecodeWeight: Received broadcast");

             if(mDeviceNumberWGT == ANTPlusDemo.WILDCARD)
             {
                 try
                 {
                     Log.i(TAG, "antDecodeWeight: Requesting device number");

                     mAntReceiver.ANTRequestMessage(WEIGHT_CHANNEL, AntMesg.MESG_CHANNEL_ID_ID);
                 }
                 catch(AntInterfaceException e)
                 {
                     antError();
                 }
             }

             if(!mHasSentProfile)
             {
                 try
                 {
                     byte[] Profile = {0x3A, 0x10, 0x00, 0x02, 
                             (byte)0xFF, (byte)0x99, (byte)0xAD, 0x04};   // Sample user profile (male, 25)
                     mAntReceiver.ANTSendBroadcastData(WEIGHT_CHANNEL, Profile);
                     mHasSentProfile = true;
                     Log.i(TAG, "Weight user profile sent");
                 }
                 catch(AntInterfaceException e)
                 {
                     antError();
                 }
             }
             else
             {               
                if(ANTRxMessage[3] == 0x01)   // check for data page 1
                {
                   mWeight = (ANTRxMessage[9]&0xFF  | ((ANTRxMessage[10]&0xFF) << 8)) & 0xFFFF;
                   if(mWeight == 0xFFFF)
                   {
                      mWeightState = ChannelStates.TRACKING_STATUS;
                      mWeightStatus = mContext.getResources().getString(R.string.Invalid);
                      mCallbackSink.notifyChannelStateChanged(WEIGHT_CHANNEL);
                   }
                   else if(mWeight == 0xFFFE)
                   {
                      mWeightState = ChannelStates.TRACKING_STATUS;
                      if(!mSessionDone)
                         mWeightStatus = mContext.getResources().getString(R.string.Computing);
                      else
                         mWeightStatus = mContext.getResources().getString(R.string.NewSession);
                      mCallbackSink.notifyChannelStateChanged(WEIGHT_CHANNEL);
                   }
                   else
                   {
                      mWeightState = ChannelStates.TRACKING_DATA;
                      mWeightStatus = mContext.getResources().getString(R.string.Connected);
                      mSessionDone = true;
                      mCallbackSink.notifyChannelStateChanged(WEIGHT_CHANNEL);
                      mCallbackSink.notifyChannelDataChanged(WEIGHT_CHANNEL);
                   }
                }
             }
          }
          Log.d(TAG, "antDecodeWeight end");
       }

       /**
        * Decode ANT+ SDM messages.
        *
        * @param ANTRxMessage the received ANT message.
        */
       private void antDecodeSDM(byte[] ANTRxMessage)
       {
          Log.d(TAG, "antDecodeSDM start");
          
          if(mAntProcessingEnabled)
          {
             Log.d(TAG, "antDecodeSDM: Received broadcast");

             if(mSdmState != ChannelStates.CLOSED)
             {
                Log.d(TAG, "antDecodeSDM: Tracking data");

                mSdmState = ChannelStates.TRACKING_DATA;
                mCallbackSink.notifyChannelStateChanged(SDM_CHANNEL);
             }
             
             // If using a wild card search, request the channel ID
             if(mDeviceNumberSDM == ANTPlusDemo.WILDCARD)
             {
                 try
                 {
                     Log.i(TAG, "antDecodeSDM: Requesting device number");

                     mAntReceiver.ANTRequestMessage(SDM_CHANNEL, AntMesg.MESG_CHANNEL_ID_ID);
                 }
                 catch(AntInterfaceException e)
                 {
                     antError();
                 }
             }

             if ((ANTRxMessage[3]) == 0x01)  //check for data page 1
             {
                mSpeed = (ANTRxMessage[7] & 0x0F) + ((ANTRxMessage[8] & 0xFF)) / 256.0f;
                float distance = (ANTRxMessage[6] & 0xFF) + (((ANTRxMessage[7] >>> 4) & 0x0F) / 16.0f);
                int strides = ANTRxMessage[9] & 0xFF;
                
                if(!mDistanceInit)   // Calculate cumulative distance
                {
                   mPrevDistance = distance;
                   mDistanceInit = true;
                }
                
                if(!mStridesInit)    // Calculate cumulative stride count
                {
                   mPrevStrides = strides;
                   mStridesInit = true;
                }          
  
                mAccumDistance += (distance - mPrevDistance);
                if(mPrevDistance > distance)
                   mAccumDistance += 256.0;
                mPrevDistance = distance;
                
                mAccumStrides += (strides - mPrevStrides);
                if(mPrevStrides > strides)
                   mAccumStrides += 256;
                mPrevStrides = strides;
             }
             if(ANTRxMessage[3] == 0x02) // check for data page 2
             {
                mCadence = (ANTRxMessage[6] & 0xFF) + (((ANTRxMessage[7] >>> 4) & 0x0F) / 16.0f);               
                mSpeed = (ANTRxMessage[7] & 0x0F) + ((ANTRxMessage[8] & 0xFF)) / 256.0f;
             }
             mCallbackSink.notifyChannelDataChanged(SDM_CHANNEL);
          }
          Log.d(TAG, "antDecodeSDM end");
       }

       /**
        * Decode ANT+ HRM messages.
        *
        * @param ANTRxMessage the received ANT message.
        */
       private void antDecodeHRM(byte[] ANTRxMessage)
       {
          Log.d(TAG, "antDecodeHRM start");
          
          if(mAntProcessingEnabled)
          {
             Log.d(TAG, "antDecodeHRM: Received broadcast");
             
             if(mHrmState != ChannelStates.CLOSED)
             {
                Log.d(TAG, "antDecodeHRMM: Tracking data");

                mHrmState = ChannelStates.TRACKING_DATA;
                mCallbackSink.notifyChannelStateChanged(HRM_CHANNEL);
             }

             if(mDeviceNumberHRM == ANTPlusDemo.WILDCARD)
             {
                 try
                 {
                     Log.i(TAG, "antDecodeHRM: Requesting device number");
                     mAntReceiver.ANTRequestMessage(HRM_CHANNEL, AntMesg.MESG_CHANNEL_ID_ID);
                 }
                 catch(AntInterfaceException e)
                 {
                     antError();
                 }
             }

             // Monitor page toggle bit
             if(mStateHRM != HRMStatePage.EXT)
             {
                if(mStateHRM == HRMStatePage.INIT)
                {
                   if((ANTRxMessage[3] & (byte) 0x80) == 0)
                      mStateHRM = HRMStatePage.TOGGLE0;
                   else
                      mStateHRM = HRMStatePage.TOGGLE1;
                }
                else if(mStateHRM == HRMStatePage.TOGGLE0)
                {
                   if((ANTRxMessage[3] & (byte) 0x80) != 0)
                      mStateHRM = HRMStatePage.EXT;
                }
                else if(mStateHRM == HRMStatePage.TOGGLE1)
                {
                   if((ANTRxMessage[3] & (byte) 0x80) == 0)
                      mStateHRM = HRMStatePage.EXT;
                }
             }
             
             // Heart rate available in all pages and regardless of toggle bit            
             mBPM = ANTRxMessage[10] & 0xFF;
             mCallbackSink.notifyChannelDataChanged(HRM_CHANNEL);
          }
          Log.d(TAG, "antDecodeHRM end");
       }
       
       private void antDecodeTest(byte[] ANTRxMessage)
       {
          Log.d(TAG, "antDecodeTest start");
          
          if(mAntProcessingEnabled)
          {
             Log.d(TAG, "antDecodeHRM: Received broadcast");
             
             if(mHrmState != ChannelStates.CLOSED)
             {
                Log.d(TAG, "antDecodeHRMM: Tracking data");

                mHrmState = ChannelStates.TRACKING_DATA;
                mCallbackSink.notifyChannelStateChanged(HRM_CHANNEL);
             }

             if(mDeviceNumberHRM == ANTPlusDemo.WILDCARD)
             {
                 try
                 {
                     Log.i(TAG, "antDecodeHRM: Requesting device number");
                     mAntReceiver.ANTRequestMessage(HRM_CHANNEL, AntMesg.MESG_CHANNEL_ID_ID);
                 }
                 catch(AntInterfaceException e)
                 {
                     antError();
                 }
             }

             
             // Heart rate available in all pages and regardless of toggle bit            
             mTest = ANTRxMessage[10];
             mCallbackSink.notifyChannelDataChanged(HRM_CHANNEL);
          }
          Log.d(TAG, "antDecodeTest end");
       }
       
      
    };
    
    
    /**
     * ANT Channel Configuration.
     *
     * @param networkNumber the network number
     * @param channelNumber the channel number
     * @param deviceNumber the device number
     * @param deviceType the device type
     * @param txType the tx type
     * @param channelPeriod the channel period
     * @param radioFreq the radio freq
     * @param proxSearch the prox search
     * @return true, if successfully configured and opened channel
     */   
    private boolean antChannelSetup(byte networkNumber, byte channelNumber, short deviceNumber, byte deviceType, byte txType, short channelPeriod, byte radioFreq, byte proxSearch)
    {
       boolean channelOpen = false;

       try
       {
           mAntReceiver.ANTAssignChannel(channelNumber, AntDefine.PARAMETER_RX_NOT_TX, networkNumber);  // Assign as slave channel on selected network (0 = public, 1 = ANT+, 2 = ANTFS)
           mAntReceiver.ANTSetChannelId(channelNumber, deviceNumber, deviceType, txType);
           mAntReceiver.ANTSetChannelPeriod(channelNumber, channelPeriod);
           mAntReceiver.ANTSetChannelRFFreq(channelNumber, radioFreq);
           mAntReceiver.ANTSetChannelSearchTimeout(channelNumber, (byte)0); // Disable high priority search
           mAntReceiver.ANTSetLowPriorityChannelSearchTimeout(channelNumber,(byte) 12); // Set search timeout to 30 seconds (low priority search)
           
           if(deviceNumber == ANTPlusDemo.WILDCARD)
           {
               mAntReceiver.ANTSetProximitySearch(channelNumber, proxSearch);   // Configure proximity search, if using wild card search
           }

           mAntReceiver.ANTOpenChannel(channelNumber);
           
           channelOpen = true;
       }
       catch(AntInterfaceException aie)
       {
           antError();
       }
      
       return channelOpen;
    }
    
    /**
     * Enable/disable receiving ANT Rx messages.
     *
     * @param register If want to register to receive the ANT Rx Messages
     */
    private void receiveAntRxMessages(boolean register)
    {
        if(register)
        {
            Log.i(TAG, "receiveAntRxMessages: START");
            mActivity.getApplicationContext().registerReceiver(mAntMessageReceiver, new IntentFilter(AntInterfaceIntent.ANT_RX_MESSAGE_ACTION));
        }
        else
        {
            try
            {
                mActivity.getApplicationContext().unregisterReceiver(mAntMessageReceiver);
            }
            catch(IllegalArgumentException e)
            {
                // Receiver wasn't registered, ignore as that's what we wanted anyway
            }

            Log.i(TAG, "receiveAntRxMessages: STOP");
        }
    }
}
