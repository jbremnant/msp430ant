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

import java.text.DecimalFormat;

import com.dsi.ant.AntDefine;

import android.app.Activity;
import android.app.Dialog;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * ANT+ Demo Activity.
 * 
 * This class implements the GUI functionality and basic interaction with the AntPlusManager class.
 * For the code that does the Ant interfacing see the AntPlusManager class.
 */
public class ANTPlusDemo extends Activity implements View.OnClickListener, AntPlusManager.Callbacks
{
   
   /** The Log Tag. */
   public static final String TAG = "ANTApp";   
   
   /**
    * The possible menu items (when pressed menu key).
    */
   private enum MyMenu
   {
      /** No menu item. */
      MENU_NONE,
      
      /** Exit menu item. */
      MENU_EXIT,      
      
      /** Pair HRM menu item. */
      MENU_PAIR_HRM,
      
      /** Pair SDM menu item. */
      MENU_PAIR_SDM,
      
      /** Pair weight scale menu item. */
      MENU_PAIR_WEIGHT,
      
      /** Sensor Configuration menu item. */
      MENU_CONFIG,
      
      /** Configure HRM menu item. */
      MENU_CONFIG_HRM,
      
      /** Configure SDM menu item. */
      MENU_CONFIG_SDM,
      
      /** Configure Weight Scale menu item. */
      MENU_CONFIG_WGT,
      
      /** Configure Proximity menu item. */
      MENU_CONFIG_PROXIMITY,
      
      /** Configure Buffer Threshold menu item. */
      MENU_CONFIG_BUFFER_THRESHOLD,
      
      /** Send a request to claim the ANT Interface. */
      MENU_REQUEST_CLAIM_INTERFACE,
   }

   /** The key for referencing the requestClaimInterface variable in saved instance data. */
   static final String REQUESTING_CLAIM_ANT_INTERFACE_KEY = "request_claim_ant_interface";
   
   /** Displays ANT state. */
   private TextView mAntStateText;

   /** Formatter used during printing of data */
   private DecimalFormat mOutputFormatter;
   
   /** Pair to any device. */
   static final short WILDCARD = 0;
   
   /** The default proximity search bin. */
   private static final byte DEFAULT_BIN = 7;
   
   /** The default event buffering buffer threshold. */
   private static final short DEFAULT_BUFFER_THRESHOLD = 0;
   
   /** Device ID valid value range. */
   private static final String mDeviceIdHint = AntDefine.MIN_DEVICE_ID +" - "+ (AntDefine.MAX_DEVICE_ID & 0xFFFF);
   
   /** Proximity Bin valid value range. */
   private static final String mBinHint = AntDefine.MIN_BIN +" - "+ AntDefine.MAX_BIN;
   
   /** Buffer Threshold valid value range. */
   private static final String mBufferThresholdHint = AntDefine.MIN_BUFFER_THRESHOLD +" - "+ AntDefine.MAX_BUFFER_THRESHOLD;

   /** Shared preferences data filename. */
   public static final String PREFS_NAME = "ANTDemoPrefs";
   
   /** Class to manage all the ANT messaging and setup */
   private AntPlusManager mAntManager;

   
   @Override
   public void onCreate(Bundle savedInstanceState) 
   {
       super.onCreate(savedInstanceState);
       Log.d(TAG, "onCreate enter");
       
       if(!this.isFinishing())
       {
           setContentView(R.layout.main);  
           initControls();
       }
       
       mAntManager = (AntPlusManager) getLastNonConfigurationInstance();
       if(mAntManager == null)
       {
           mAntManager = new AntPlusManager(this, savedInstanceState, this);
           
           // Always have ANT service connected
           mAntManager.start();
       }
       else
       {
           mAntManager.resetCallbacks(this, this);
       }
       
       mOutputFormatter = new DecimalFormat(getString(R.string.DataFormat));

       Log.d(TAG, "onCreate exit");
   }


   @Override
   public Object onRetainNonConfigurationInstance() 
   {
       //Save the current service connection
       return mAntManager;
   }
   

   @Override protected void onDestroy()
   {
      Log.d(TAG, "onDestroy enter");
      
      if(isFinishing())
      {
          mAntManager.shutDown();
      }

      mAntManager = null;

      super.onDestroy();

      Log.d(TAG, "onDestroy exit");
   }   


   @Override
   public boolean onCreateOptionsMenu(Menu menu) 
   {      
       boolean result = super.onCreateOptionsMenu(menu);
       
       if(mAntManager.isServiceConnected())
       {
           menu.add(Menu.NONE, MyMenu.MENU_PAIR_HRM.ordinal(), 0, this.getResources().getString(R.string.Menu_Wildcard_HRM));
           menu.add(Menu.NONE, MyMenu.MENU_PAIR_SDM.ordinal(), 1, this.getResources().getString(R.string.Menu_Wildcard_SDM));
           menu.add(Menu.NONE, MyMenu.MENU_PAIR_WEIGHT.ordinal(), 2, this.getResources().getString(R.string.Menu_Wildcard_Weight));

           SubMenu configMenu = menu.addSubMenu(Menu.NONE, MyMenu.MENU_CONFIG.ordinal(), 3, this.getResources().getString(R.string.Menu_Sensor_Config));
           configMenu.add(Menu.NONE,MyMenu.MENU_CONFIG_HRM.ordinal(), 0, this.getResources().getString(R.string.Menu_HRM));
           configMenu.add(Menu.NONE,MyMenu.MENU_CONFIG_SDM.ordinal(), 1, this.getResources().getString(R.string.Menu_SDM));
           configMenu.add(Menu.NONE,MyMenu.MENU_CONFIG_WGT.ordinal(), 2, this.getResources().getString(R.string.Menu_WGT));
           configMenu.add(Menu.NONE,MyMenu.MENU_CONFIG_PROXIMITY.ordinal(), 3, this.getResources().getString(R.string.Menu_Proximity));
           configMenu.add(Menu.NONE,MyMenu.MENU_CONFIG_BUFFER_THRESHOLD.ordinal(), 4, this.getResources().getString(R.string.Menu_Buffer_Threshold));
           
           menu.add(Menu.NONE, MyMenu.MENU_REQUEST_CLAIM_INTERFACE.ordinal(), 4, this.getResources().getString(R.string.Menu_Claim_Interface));
       }
      
       menu.add(Menu.NONE, MyMenu.MENU_EXIT.ordinal(), 99, this.getResources().getString(R.string.Menu_Exit));
       
       return result;
   }
   
   
   @Override
   public boolean onOptionsItemSelected(MenuItem item) 
   {
      MyMenu selectedItem = MyMenu.values()[item.getItemId()];
      switch (selectedItem) 
      {         
         case MENU_EXIT:            
            exitApplication();
            break;
         case MENU_PAIR_HRM:
            mAntManager.setDeviceNumberHRM(WILDCARD);
            break;
         case MENU_CONFIG_HRM:
            showDialog(PairingDialog.HRM_ID);
            break;
         case MENU_PAIR_SDM:
             mAntManager.setDeviceNumberSDM(WILDCARD);
            break;
         case MENU_PAIR_WEIGHT:
             mAntManager.setDeviceNumberWGT(WILDCARD);
             break;
         case MENU_CONFIG_SDM:
            showDialog(PairingDialog.SDM_ID);
            break;
         case MENU_CONFIG_WGT:
            showDialog(PairingDialog.WGT_ID);
            break;
         case MENU_CONFIG_PROXIMITY:
            showDialog(PairingDialog.PROX_ID);
            break;
         case MENU_CONFIG_BUFFER_THRESHOLD:
            showDialog(PairingDialog.BUFF_ID);
            break;
         case MENU_REQUEST_CLAIM_INTERFACE:
             mAntManager.tryClaimAnt();
             break;
         case MENU_CONFIG:
             //fall through to do nothing, as this represents a submenu, not a menu option
         case MENU_NONE:
             //Do nothing for these, as they shouldn't even be registered as menu options
             break;
      }
      return super.onOptionsItemSelected(item);
   }

   
   @Override
   protected PairingDialog onCreateDialog(int id)
   {
      PairingDialog theDialog = null;
      
      if(id == PairingDialog.HRM_ID)
         theDialog = new PairingDialog(this, id, mAntManager.getDeviceNumberHRM(), mDeviceIdHint, new OnPairingListener());
      else if(id == PairingDialog.SDM_ID)
         theDialog = new PairingDialog(this, id, mAntManager.getDeviceNumberSDM(), mDeviceIdHint, new OnPairingListener());
      else if(id == PairingDialog.WGT_ID)
         theDialog = new PairingDialog(this, id, mAntManager.getDeviceNumberWGT(), mDeviceIdHint, new OnPairingListener());
      else if(id == PairingDialog.PROX_ID)
         theDialog = new PairingDialog(this, id, mAntManager.getProximityThreshold(), mBinHint, new OnPairingListener());
      else if(id == PairingDialog.BUFF_ID)
          theDialog = new PairingDialog(this, id, mAntManager.getBufferThreshold(), mBufferThresholdHint, new OnPairingListener());
      return theDialog;
   }
   
   /**
    * Listener to updates to the device number.
    *
    * @see OnPairingEvent
    */
   private class OnPairingListener implements PairingDialog.PairingListener 
   {
      
      /* (non-Javadoc)
       * @see com.dsi.ant.antplusdemo.PairingDialog.PairingListener#updateID(int, short)
       */
      public void updateID(int id, short deviceNumber)
      {
         if(id == PairingDialog.HRM_ID)
            mAntManager.setDeviceNumberHRM(deviceNumber);
         else if(id == PairingDialog.SDM_ID)
             mAntManager.setDeviceNumberSDM(deviceNumber);
         else if(id == PairingDialog.WGT_ID)
             mAntManager.setDeviceNumberWGT(deviceNumber);
         else if(id == PairingDialog.BUFF_ID)
         {
             mAntManager.setBufferThreshold(deviceNumber);
             
             mAntManager.setAntConfiguration();
         }
      }
      
      /* (non-Javadoc)
       * @see com.dsi.ant.antplusdemo.PairingDialog.PairingListener#updateThreshold(int, byte)
       */
      public void updateThreshold(int id, byte proxThreshold)
      {
         if(id == PairingDialog.PROX_ID)
            mAntManager.setProximityThreshold(proxThreshold);
      }
   }
  

   @Override
   protected void onPrepareDialog(int id, Dialog theDialog)
   {
      super.onPrepareDialog(id, theDialog);
      PairingDialog dialog = (PairingDialog) theDialog;
      dialog.setId(id);
      if(id == PairingDialog.HRM_ID)
      {
         dialog.setDeviceNumber(mAntManager.getDeviceNumberHRM());
      }
      else if(id == PairingDialog.SDM_ID)
      {
         dialog.setDeviceNumber(mAntManager.getDeviceNumberSDM());
      }
      else if(id == PairingDialog.WGT_ID)
      {
         dialog.setDeviceNumber(mAntManager.getDeviceNumberWGT());
      }
      else if(id == PairingDialog.PROX_ID)
      {
         dialog.setProximityThreshold(mAntManager.getProximityThreshold());
      }
      else if(id == PairingDialog.BUFF_ID)
      {
         dialog.setDeviceNumber(mAntManager.getBufferThreshold());
      }
   }
   

   @Override
   protected void onRestoreInstanceState(Bundle savedInstanceState)
   {
      Log.d(TAG, "onRestoreInstanceState");
      
      mAntManager.loadState(savedInstanceState);

      super.onRestoreInstanceState(savedInstanceState);
   }
   
    // It is important to save persistent data in onPause() instead of onSaveInstanceState(Bundle)
    // because the later is not part of the lifecycle callbacks, so will not be called in every
    // situation as described in its documentation.
   @Override
   protected void onSaveInstanceState(Bundle outState)
   {
      super.onSaveInstanceState(outState);
      mAntManager.saveState(outState);
      Log.d(TAG, "onSaveInstanceState");
   }


   @Override
   protected void onPause()
   {
      Log.d(TAG, "onPause");

      saveState();

      // disable some processing of ANT messages while UI is not available
      mAntManager.pauseMessageProcessing();
      
      super.onPause();      
   }
   

   @Override    
   protected void onResume()
   {
      Log.d(TAG, "onResume");

      super.onResume();
      
      loadDefaultConfiguration();

      mAntManager.resumeMessageProcessing();
   }
   
   /**
    * Store application persistent data.
    */
   private void saveState()
   {
      // Save current Channel Id in preferences
      // We need an Editor object to make changes
      SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
      SharedPreferences.Editor editor = settings.edit();
      editor.putInt("DeviceNumberHRM", mAntManager.getDeviceNumberHRM());
      editor.putInt("DeviceNumberSDM", mAntManager.getDeviceNumberSDM());
      editor.putInt("DeviceNumberWGT", mAntManager.getDeviceNumberWGT());
      editor.putInt("ProximityThreshold", mAntManager.getProximityThreshold());
      editor.putInt("BufferThreshold", mAntManager.getBufferThreshold());
      editor.commit();
   }
   
   /**
    * Retrieve application persistent data.
    */
   private void loadDefaultConfiguration()
   {
      // Restore preferences
      SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
      mAntManager.setDeviceNumberHRM((short) settings.getInt("DeviceNumberHRM", WILDCARD));
      mAntManager.setDeviceNumberSDM((short) settings.getInt("DeviceNumberSDM", WILDCARD));
      mAntManager.setDeviceNumberWGT((short) settings.getInt("DeviceNumberWGT", WILDCARD));
      mAntManager.setProximityThreshold((byte) settings.getInt("ProximityThreshold", DEFAULT_BIN));
      mAntManager.setBufferThreshold((short) settings.getInt("BufferThreshold", DEFAULT_BUFFER_THRESHOLD));
   }
   
   /**
    * Initialize GUI elements.
    */
   private void initControls()
   {
      mAntStateText = (TextView)findViewById(R.id.text_status);

      
      // Set up button listeners and scaling 
      ((ImageButton)findViewById(R.id.button_heart)).setOnClickListener(this);
      ((ImageButton)findViewById(R.id.button_heart)).setScaleType(ImageView.ScaleType.CENTER_INSIDE);
      ((ImageButton)findViewById(R.id.button_heart)).setBackgroundColor(Color.TRANSPARENT);
      ((ImageButton)findViewById(R.id.button_sdm)).setOnClickListener(this);
      ((ImageButton)findViewById(R.id.button_sdm)).setScaleType(ImageView.ScaleType.CENTER_INSIDE);
      ((ImageButton)findViewById(R.id.button_sdm)).setBackgroundColor(Color.TRANSPARENT);
      ((ImageButton)findViewById(R.id.button_weight)).setOnClickListener(this);
      ((ImageButton)findViewById(R.id.button_weight)).setScaleType(ImageView.ScaleType.CENTER_INSIDE);
      ((ImageButton)findViewById(R.id.button_weight)).setBackgroundColor(Color.TRANSPARENT);
   }
   
   /**
    * Shows/hides the channels based on the state of the ant service
    */
   private void drawWindow()
   {
       boolean showChannels = mAntManager.checkAntState();
       setDisplay(showChannels);
       if(showChannels)
       {
           drawChannel(AntPlusManager.HRM_CHANNEL);
           drawChannel(AntPlusManager.SDM_CHANNEL);
           drawChannel(AntPlusManager.WEIGHT_CHANNEL);
       }
       else
       {
           mAntStateText.setText(mAntManager.getAntStateText());
       }
   }
   
   /**
    * Sets the channel button image and status strings according to the specified channel's state
    * @param channel
    */
   private void drawChannel(byte channel)
   {
       switch(channel)
       {
           case AntPlusManager.HRM_CHANNEL:
               switch (mAntManager.getHrmState()) {
                   case CLOSED:
                       ((ImageButton)findViewById(R.id.button_heart)).setImageResource(R.drawable.ant_hrm_gray);
                       ((TextView)findViewById(R.id.text_status_hrm)).setText(getString(R.string.Closed));
                       break;
                   case OFFLINE:
                       ((ImageButton)findViewById(R.id.button_heart)).setImageResource(R.drawable.ant_hrm_gray);
                       ((TextView)findViewById(R.id.text_status_hrm)).setText(getString(R.string.NoSensor_txt));
                       break;
                   case SEARCHING:
                       //From the user's point of view waiting for a device to connect and
                       //waiting for the channel to open are equivalent.
                   case PENDING_OPEN:
                       ((ImageButton)findViewById(R.id.button_heart)).setImageResource(R.drawable.ant_hrm);
                       ((TextView)findViewById(R.id.text_status_hrm)).setText(getString(R.string.Search));
                       break;
                   case TRACKING_STATUS:
                       //This state should not show up for this channel, but in the case it does
                       //We can consider it equivalent to showing the data.
                   case TRACKING_DATA:
                       ((ImageButton)findViewById(R.id.button_heart)).setImageResource(R.drawable.ant_hrm);
                       ((TextView)findViewById(R.id.text_status_hrm)).setText(getString(R.string.Connected));
                       break;
               }
               break;
           case AntPlusManager.SDM_CHANNEL:
               switch (mAntManager.getSdmState()) {
                   case CLOSED:
                       ((ImageButton)findViewById(R.id.button_sdm)).setImageResource(R.drawable.ant_spd_gray);
                       ((TextView)findViewById(R.id.text_status_sdm)).setText(getString(R.string.Closed));
                       break;
                   case OFFLINE:
                       ((ImageButton)findViewById(R.id.button_sdm)).setImageResource(R.drawable.ant_spd_gray);
                       ((TextView)findViewById(R.id.text_status_sdm)).setText(getString(R.string.NoSensor_txt));
                       break;
                   case SEARCHING:
                       //From the user's point of view waiting for a device to connect and
                       //waiting for the channel to open are equivalent.
                   case PENDING_OPEN:
                       ((ImageButton)findViewById(R.id.button_sdm)).setImageResource(R.drawable.ant_spd);
                       ((TextView)findViewById(R.id.text_status_sdm)).setText(getString(R.string.Search));
                       break;
                   case TRACKING_STATUS:
                       //This state should not show up for this channel, but in the case it does
                       //We can consider it equivalent to showing the data.
                   case TRACKING_DATA:
                       ((ImageButton)findViewById(R.id.button_sdm)).setImageResource(R.drawable.ant_spd);
                       ((TextView)findViewById(R.id.text_status_sdm)).setText(getString(R.string.Connected));
                       break;
               }
               break;
           case AntPlusManager.WEIGHT_CHANNEL:
               switch (mAntManager.getWeightState()) {
                   case CLOSED:
                       ((ImageButton)findViewById(R.id.button_weight)).setImageResource(R.drawable.ant_wgt_gray);
                       ((TextView)findViewById(R.id.text_status_weight)).setText(getString(R.string.Closed));
                       break;
                   case OFFLINE:
                       ((ImageButton)findViewById(R.id.button_weight)).setImageResource(R.drawable.ant_wgt_gray);
                       ((TextView)findViewById(R.id.text_status_weight)).setText(getString(R.string.NoSensor_txt));
                       break;
                   case SEARCHING:
                       //From the user's point of view waiting for a device to connect and
                       //waiting for the channel to open are equivalent.
                   case PENDING_OPEN:
                       ((ImageButton)findViewById(R.id.button_weight)).setImageResource(R.drawable.ant_wgt);
                       ((TextView)findViewById(R.id.text_status_weight)).setText(getString(R.string.Search));
                       break;
                   case TRACKING_DATA:
                       //Data and status are both represented by the same string, so we do not need to
                       //differentiate between those states here.
                   case TRACKING_STATUS:
                       ((ImageButton)findViewById(R.id.button_weight)).setImageResource(R.drawable.ant_wgt);
                       ((TextView)findViewById(R.id.text_status_weight)).setText(mAntManager.getWeightStatus());
                       break;
               }
               break;
       }
       drawChannelData(channel);
   }
   
   /**
    * Fills in the data fields for the specified ant channel's data display 
    * @param channel
    */
   private void drawChannelData(byte channel)
   {
       switch(channel)
       {
           case AntPlusManager.HRM_CHANNEL:
               switch (mAntManager.getHrmState()) {
                   case CLOSED:
                   case OFFLINE:
                   case SEARCHING:
                   case PENDING_OPEN:
                       //For all these cases we don't have any incoming data, so they all show '--'
                       ((TextView)findViewById(R.id.text_bpm)).setText(getString(R.string.noData) + getString(R.string.heartRateUnits));
                       break;
                   case TRACKING_STATUS:
                       //There is no Status state for the HRM channel, so we will attempt to show latest data instead
                   case TRACKING_DATA:
                       // ((TextView)findViewById(R.id.text_bpm)).setText(mAntManager.getBPM() + getString(R.string.heartRateUnits));
                	   byte[] num = { (byte) mAntManager.getTest() };
                	   String hex = AntPlusManager.getHexString(num);
                	   ((TextView)findViewById(R.id.text_bpm)).setText(hex + " " + mAntManager.getTest() + getString(R.string.heartRateUnits));
                       break;
               }
               break;
           case AntPlusManager.SDM_CHANNEL:
               switch (mAntManager.getSdmState()) {
                   case CLOSED:
                   case OFFLINE:
                   case SEARCHING:
                   case PENDING_OPEN:
                       //For all these cases we don't have any incoming data, so they all show '--'
                       ((TextView)findViewById(R.id.text_speed)).setText(getString(R.string.noData) + getString(R.string.speedUnits));
                       ((TextView)findViewById(R.id.text_distance)).setText(getString(R.string.noData) + getString(R.string.distanceUnits));
                       ((TextView)findViewById(R.id.text_strides)).setText(getString(R.string.noData) + getString(R.string.stepUnits));
                       ((TextView)findViewById(R.id.text_cadence)).setText(getString(R.string.noData) + getString(R.string.cadenceUnits));
                       break;
                   case TRACKING_STATUS:
                       //There is no Status state for the SDM channel, so we will attempt to show latest data instead
                   case TRACKING_DATA:
                       ((TextView)findViewById(R.id.text_speed)).setText(mOutputFormatter.format(mAntManager.getSpeed()) + getString(R.string.speedUnits));
                       ((TextView)findViewById(R.id.text_distance)).setText(mOutputFormatter.format(mAntManager.getAccumDistance()) + getString(R.string.distanceUnits));
                       ((TextView)findViewById(R.id.text_strides)).setText(mAntManager.getAccumStrides() + getString(R.string.stepUnits));
                       ((TextView)findViewById(R.id.text_cadence)).setText(mOutputFormatter.format(mAntManager.getCadence()) + getString(R.string.cadenceUnits));
                       break;
               }
               break;
           case AntPlusManager.WEIGHT_CHANNEL:
               switch (mAntManager.getWeightState()) {
                   case CLOSED:
                   case OFFLINE:
                   case SEARCHING:
                   case PENDING_OPEN:
                   case TRACKING_STATUS:
                       //For all these cases we don't have any incoming data, so they all show '--'
                       ((TextView)findViewById(R.id.text_weight)).setText(getString(R.string.noData) + getString(R.string.weightUnits));
                       break;
                   case TRACKING_DATA:
                       ((TextView)findViewById(R.id.text_weight)).setText(mOutputFormatter.format(mAntManager.getWeight()/100.0) + getString(R.string.weightUnits));
                       break;
               }
               break;
       }
   }
   
   /**
    * Set whether buttons etc are visible.
    *
    * @param pVisible buttons visible, status text shown when they are not.
    */
   protected void setDisplay(boolean pVisible)
   {
       Log.d(TAG, "setDisplay: visible = "+ pVisible);
       
       int visibility = (pVisible ? View.VISIBLE : View.INVISIBLE);

       findViewById(R.id.button_heart).setVisibility(visibility);
       findViewById(R.id.hrm_layout).setVisibility(visibility);
       findViewById(R.id.button_sdm).setVisibility(visibility);
       findViewById(R.id.sdm_layout).setVisibility(visibility);
       findViewById(R.id.button_weight).setVisibility(visibility);
       findViewById(R.id.weight_layout).setVisibility(visibility);
       
       if(!pVisible)
       {
           mAntManager.clearChannelStates();
       }

       mAntStateText.setVisibility(pVisible ? TextView.INVISIBLE : TextView.VISIBLE); // Visible when buttons aren't
   }


   /**
    * Display alert dialog.
    *
    * @param context the context to use
    * @param title the dialog title
    * @param msg the message to display in the dialog
    */
   public void showAlert(Context context, String title, String msg) 
   {
      new AlertDialog.Builder(context).setTitle(title).setIcon(
            android.R.drawable.ic_dialog_alert).setMessage(msg)
            .setNegativeButton(android.R.string.cancel, null).show();
   }
   
   // OnClickListener implementation.
   public void onClick(View v)
   {
        // If no channels are open, reset ANT
        if (!mAntManager.isChannelOpen(AntPlusManager.HRM_CHANNEL)
                && !mAntManager.isChannelOpen(AntPlusManager.SDM_CHANNEL)
                && !mAntManager.isChannelOpen(AntPlusManager.WEIGHT_CHANNEL))
        {
            Log.d(TAG, "onClick: No channels open, reseting ANT");
            // Defer opening the channel until an ANT_RESET has been
            // received
            switch (v.getId())
            {
                case R.id.button_heart:
                    mAntManager.openChannel(AntPlusManager.HRM_CHANNEL, true);
                    break;
                case R.id.button_sdm:
                    mAntManager.openChannel(AntPlusManager.SDM_CHANNEL, true);
                    break;
                case R.id.button_weight:
                    mAntManager.openChannel(AntPlusManager.WEIGHT_CHANNEL, true);
                    break;
            }
            mAntManager.requestReset();
        }
        else {
            switch (v.getId()) {
                case R.id.button_heart:
                    if (!mAntManager.isChannelOpen(AntPlusManager.HRM_CHANNEL))
                    {
                        // Configure and open channel
                        Log.d(TAG, "onClick (HRM): Open channel");
                        mAntManager.openChannel(AntPlusManager.HRM_CHANNEL, false);
                    } else
                    {
                        // Close channel
                        Log.d(TAG, "onClick (HRM): Close channel");
                        mAntManager.closeChannel(AntPlusManager.HRM_CHANNEL);
                    }
                    break;
                case R.id.button_sdm:
                    if (!mAntManager.isChannelOpen(AntPlusManager.SDM_CHANNEL))
                    {
                        // Configure and open channel
                        Log.d(TAG, "onClick (SDM): Open channel");
                        mAntManager.openChannel(AntPlusManager.SDM_CHANNEL, false);
                    } else
                    {
                        // Close channel
                        Log.d(TAG, "onClick (SDM): Close channel");
                        mAntManager.closeChannel(AntPlusManager.SDM_CHANNEL);
                    }
                    break;
                case R.id.button_weight:
                    if (!mAntManager.isChannelOpen(AntPlusManager.WEIGHT_CHANNEL))
                    {
                        // Configure and open channel
                        Log.d(TAG, "onClick (Weight): Open channel");
                        mAntManager.openChannel(AntPlusManager.WEIGHT_CHANNEL, false);
                    } else
                    {
                        // Close channel
                        Log.d(TAG, "onClick (Weight): Close channel");
                        mAntManager.closeChannel(AntPlusManager.WEIGHT_CHANNEL);
                    }
                    break;
            }
        }
   }
   
   //Implementations of the AntPlusManager call backs

   @Override
   public void errorCallback()
   {
       setDisplay(false);
   }

   @Override
   public void notifyAntStateChanged()
   {
       drawWindow();
   }
   
   @Override
   public void notifyChannelStateChanged(byte channel)
   {
       drawChannel(channel);
   }
   
   @Override
   public void notifyChannelDataChanged(byte channel)
   {
       drawChannelData(channel);
   }
   

   /**
    * Exit application.
    */
   private void exitApplication()
   {
      Log.d(TAG, "exitApplication enter");
      
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      
      builder.setMessage(this.getResources().getString(R.string.Dialog_Exit_Check));
      builder.setCancelable(false);

      builder.setPositiveButton(this.getResources().getString(R.string.Dialog_Confirm), new DialogInterface.OnClickListener() {
                 public void onClick(DialogInterface dialog, int id) {
                     Log.i(TAG, "exitApplication: Exit");
                     finish();
                 }
             });

      builder.setNegativeButton(this.getResources().getString(R.string.Dialog_Cancel), new DialogInterface.OnClickListener() {
                 public void onClick(DialogInterface dialog, int id) {
                     Log.i(TAG, "exitApplication: Cancelled");
                     dialog.cancel();
                 }
             });

      AlertDialog exitDialog = builder.create();
      exitDialog.show();
   }
}
