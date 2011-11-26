/*
 * Dynastream Innovations Inc.
 * Cochrane, AB, CANADA
 *
 * Copyright (c) 2008 Dynastream Innovations Inc.
 * All rights reserved. This software may not be reproduced by
 * any means without express written approval of Dynastream
 * Innovations Inc.
 */

#ifndef ANTMESSAGE_H
#define ANTMESSAGE_H

#include "types.h"

/////////////////////////////////////////////////////////////////////////////
// Message Format
// Messages are in the format:
//
// AX XX YY -------- CK
//
// where: AX    is the 1 byte sync byte either transmit or recieve
//        XX    is the 1 byte size of the message (0-249) NOTE: THIS WILL BE LIMITED BY THE EMBEDDED RECEIVE BUFFER SIZE
//        YY    is the 1 byte ID of the message (1-255, 0 is invalid)
//        ----- is the data of the message (0-249 bytes of data)
//        CK    is the 1 byte Checksum of the message
/////////////////////////////////////////////////////////////////////////////
#define MESG_TX_SYNC                      ((UCHAR)0xA4)
#define MESG_RX_SYNC                      ((UCHAR)0xA5)
#define MESG_SIZE_OFFSET                  ((UCHAR)1)    
#define MESG_ID_OFFSET                    ((UCHAR)2)     
#define MESG_SYNC_SIZE                    ((UCHAR)1)
#define MESG_SIZE_SIZE                    ((UCHAR)1)
#define MESG_ID_SIZE                      ((UCHAR)1)
#define MESG_CHECKSUM_SIZE                ((UCHAR)1)
#define MESG_MAX_DATA_SIZE                ((UCHAR)17)
#define MESG_HEADER_SIZE                  (MESG_SYNC_SIZE + MESG_SIZE_SIZE + MESG_ID_SIZE)
#define MESG_DATA_OFFSET                  MESG_HEADER_SIZE  
#define MESG_FRAME_SIZE                   (MESG_HEADER_SIZE + MESG_CHECKSUM_SIZE)
#define MESG_MAX_SIZE                     (MESG_MAX_DATA_SIZE + MESG_FRAME_SIZE)

//////////////////////////////////////////////
// Message ID's
//////////////////////////////////////////////
#define MESG_INVALID_ID                   ((UCHAR)0x00)
#define MESG_EVENT_ID                     ((UCHAR)0x01)

#define MESG_VERSION_ID                   ((UCHAR)0x3E)  // protocol library version
#define MESG_RESPONSE_EVENT_ID            ((UCHAR)0x40)

#define MESG_UNASSIGN_CHANNEL_ID          ((UCHAR)0x41)
#define MESG_ASSIGN_CHANNEL_ID            ((UCHAR)0x42)
#define MESG_CHANNEL_MESG_PERIOD_ID       ((UCHAR)0x43)
#define MESG_CHANNEL_SEARCH_TIMEOUT_ID    ((UCHAR)0x44)
#define MESG_CHANNEL_RADIO_FREQ_ID        ((UCHAR)0x45)
#define MESG_NETWORK_KEY_ID               ((UCHAR)0x46)
#define MESG_RADIO_TX_POWER_ID            ((UCHAR)0x47)
#define MESG_RADIO_CW_MODE_ID             ((UCHAR)0x48)
#define MESG_SEARCH_WAVEFORM_ID           ((UCHAR)0x49)

#define MESG_SYSTEM_RESET_ID              ((UCHAR)0x4A)
#define MESG_OPEN_CHANNEL_ID              ((UCHAR)0x4B)
#define MESG_CLOSE_CHANNEL_ID             ((UCHAR)0x4C)
#define MESG_REQUEST_ID                   ((UCHAR)0x4D)

#define MESG_BROADCAST_DATA_ID            ((UCHAR)0x4E)
#define MESG_ACKNOWLEDGED_DATA_ID         ((UCHAR)0x4F)
#define MESG_BURST_DATA_ID                ((UCHAR)0x50)

#define MESG_CHANNEL_ID_ID                ((UCHAR)0x51)
#define MESG_CHANNEL_STATUS_ID            ((UCHAR)0x52)
#define MESG_RADIO_CW_INIT_ID             ((UCHAR)0x53)
#define MESG_CAPABILITIES_ID              ((UCHAR)0x54)

#define MESG_NVM_DATA_ID                  ((UCHAR)0x56)
#define MESG_NVM_CMD_ID                   ((UCHAR)0x57)
#define MESG_NVM_STRING_ID                ((UCHAR)0x58)

#define MESG_ID_LIST_ADD_ID               ((UCHAR)0x59)
#define MESG_ID_LIST_CONFIG_ID            ((UCHAR)0x5A)
#define MESG_OPEN_RX_SCAN_ID              ((UCHAR)0x5B)

#define MESG_EXT_CHANNEL_RADIO_FREQ_ID    ((UCHAR)0x5C)
#define MESG_EXT_BROADCAST_DATA_ID        ((UCHAR)0x5D)
#define MESG_EXT_ACKNOWLEDGED_DATA_ID     ((UCHAR)0x5E)
#define MESG_EXT_BURST_DATA_ID            ((UCHAR)0x5F)

#define MESG_CHANNEL_RADIO_TX_POWER_ID    ((UCHAR)0x60)
#define MESG_GET_SERIAL_NUM_ID            ((UCHAR)0x61)
#define MESG_GET_TEMP_CAL_ID              ((UCHAR)0x62)
#define MESG_SET_LP_SEARCH_TIMEOUT_ID     ((UCHAR)0x63)
#define MESG_SET_TX_SEARCH_ON_NEXT_ID     ((UCHAR)0x64)
#define MESG_SERIAL_NUM_SET_CHANNEL_ID_ID ((UCHAR)0x65)
#define MESG_RX_EXT_MESGS_ENABLE_ID       ((UCHAR)0x66)
#define MESG_RADIO_CONFIG_ALWAYS_ID       ((UCHAR)0x67)
#define MESG_ENABLE_LED_FLASH_ID          ((UCHAR)0x68)

#define MESG_AGC_CONFIG_ID                ((UCHAR)0x6A)

#define MESG_READ_SEGA_ID                 ((UCHAR)0xA0)
#define MESG_SEGA_CMD_ID                  ((UCHAR)0xA1)
#define MESG_SEGA_DATA_ID                 ((UCHAR)0xA2)
#define MESG_SEGA_ERASE_ID                ((UCHAR)0XA3)   
#define MESG_SEGA_WRITE_ID                ((UCHAR)0XA4)
//                                        ((UCHAR)0xA5) //FREE
#define MESG_SEGA_LOCK_ID                 ((UCHAR)0xA6)
#define MESG_FUSECHECK_ID                 ((UCHAR)0xA7)
#define MESG_UARTREG_ID                   ((UCHAR)0XA8)
#define MESG_MAN_TEMP_ID                  ((UCHAR)0xA9)
#define MESG_BIST_ID                      ((UCHAR)0XAA)
#define MESG_SELFERASE_ID                 ((UCHAR)0XAB)
#define MESG_SET_MFG_BITS_ID              ((UCHAR)0xAC)
#define MESG_UNLOCK_INTERFACE_ID          ((UCHAR)0xAD)

#define MESG_IO_STATE_ID                  ((UCHAR)0xB0)
#define MESG_CFG_STATE_ID                 ((UCHAR)0xB1)

#define MESG_RSSI_ID                      ((UCHAR)0xC0)
#define MESG_RSSI_BROADCAST_DATA_ID       ((UCHAR)0xC1)
#define MESG_RSSI_ACKNOWLEDGED_DATA_ID    ((UCHAR)0xC2)
#define MESG_RSSI_BURST_DATA_ID           ((UCHAR)0xC3)
#define MESG_RSSI_SEARCH_THRESHOLD_ID     ((UCHAR)0xC4)

#define MESG_BTH_BROADCAST_DATA_ID        ((UCHAR)0xD0)
#define MESG_BTH_ACKNOWLEDGED_DATA_ID     ((UCHAR)0xD1)
#define MESG_BTH_BURST_DATA_ID            ((UCHAR)0xD2)

#define MESG_BTH_EXT_BROADCAST_DATA_ID    ((UCHAR)0xD3)
#define MESG_BTH_EXT_ACKNOWLEDGED_DATA_ID ((UCHAR)0xD4)
#define MESG_BTH_EXT_BURST_DATA_ID        ((UCHAR)0xD5)

//////////////////////////////////////////////
// Message Sizes
//////////////////////////////////////////////
#define MESG_INVALID_SIZE                 ((UCHAR)0)

#define MESG_RESPONSE_EVENT_SIZE          ((UCHAR)3)
#define MESG_CHANNEL_STATUS_SIZE          ((UCHAR)2)
#define MESG_VERSION_SIZE                 ((UCHAR)9)

#define MESG_UNASSIGN_CHANNEL_SIZE        ((UCHAR)1)
#define MESG_ASSIGN_CHANNEL_SIZE          ((UCHAR)3)
#define MESG_CHANNEL_ID_SIZE              ((UCHAR)5)
#define MESG_CHANNEL_MESG_PERIOD_SIZE     ((UCHAR)3)
#define MESG_CHANNEL_SEARCH_TIMEOUT_SIZE  ((UCHAR)2)
#define MESG_CHANNEL_RADIO_FREQ_SIZE      ((UCHAR)2)
#define MESG_NETWORK_KEY_SIZE             ((UCHAR)9)
#define MESG_RADIO_TX_POWER_SIZE          ((UCHAR)2)
#define MESG_RADIO_CW_MODE_SIZE           ((UCHAR)3)
#define MESG_RADIO_CW_INIT_SIZE           ((UCHAR)1)
#define MESG_SEARCH_WAVEFORM_SIZE         ((UCHAR)3)

#define MESG_SYSTEM_RESET_SIZE            ((UCHAR)1)
#define MESG_OPEN_CHANNEL_SIZE            ((UCHAR)1)
#define MESG_CLOSE_CHANNEL_SIZE           ((UCHAR)1)
#define MESG_REQUEST_SIZE                 ((UCHAR)2)
#define MESG_CAPABILITIES_SIZE            ((UCHAR)6)

#define MESG_DATA_SIZE                    ((UCHAR)9)

#define MESG_NVM_DATA_SIZE                ((UCHAR)10)
#define MESG_NVM_CMD_SIZE                 ((UCHAR)3)
#define MESG_NVM_STRING_SIZE              ((UCHAR)9)

#define MESG_ID_LIST_ADD_SIZE             ((UCHAR)6)
#define MESG_ID_LIST_CONFIG_SIZE          ((UCHAR)3)
#define MESG_OPEN_RX_SCAN_SIZE            ((UCHAR)1)
#define MESG_EXT_CHANNEL_RADIO_FREQ_SIZE  ((UCHAR)3)
#define MESG_EXT_DATA_SIZE                ((UCHAR)13)

#define MESG_RADIO_CONFIG_ALWAYS_SIZE     ((UCHAR)2)
#define MESG_RX_EXT_MESGS_ENABLE_SIZE     ((UCHAR)2)
#define MESG_SET_TX_SEARCH_ON_NEXT_SIZE   ((UCHAR)2)
#define MESG_SET_LP_SEARCH_TIMEOUT_SIZE   ((UCHAR)2)

#define MESG_SERIAL_NUM_SET_CHANNEL_ID_SIZE ((UCHAR)3)
#define MESG_ENABLE_LED_FLASH_SIZE        ((UCHAR)2)
#define MESG_GET_SERIAL_NUM_SIZE          ((UCHAR)4)
#define MESG_GET_TEMP_CAL_SIZE            ((UCHAR)4)

#define MESG_AGC_CONFIG_SIZE              ((UCHAR)2)

#define MESG_READ_SEGA_SIZE               ((UCHAR)2)
#define MESG_SEGA_CMD_SIZE                ((UCHAR)3)
#define MESG_SEGA_DATA_SIZE               ((UCHAR)10)
#define MESG_SEGA_ERASE_SIZE              ((UCHAR)0)
#define MESG_SEGA_WRITE_SIZE              ((UCHAR)3)
#define MESG_SEGA_LOCKED_SIZE             ((UCHAR)1)
#define MESG_SEGA_LOCK_SIZE               ((UCHAR)0)
#define MESG_FUSECHECK_SIZE               ((UCHAR)1)
#define MESG_UARTREG_SIZE                 ((UCHAR)2)
#define MESG_MAN_TEMP_SIZE                ((UCHAR)2)
#define MESG_BIST_SIZE                    ((UCHAR)6)
#define MESG_SELFERASE_SIZE               ((UCHAR)2)

#define MESG_SET_MFG_BITS_SIZE            ((UCHAR)2)
#define MESG_UNLOCK_INTERFACE_SIZE        ((UCHAR)1)

#define MESG_IO_STATE_SIZE                ((UCHAR)2)
#define MESG_CFG_STATE_SIZE               ((UCHAR)2)

#define MESG_RSSI_SIZE                    ((UCHAR)3)
#define MESG_RSSI_DATA_SIZE               ((UCHAR)17)
#define MESG_RSSI_SEARCH_THRESHOLD_SIZE   ((UCHAR)2)

#endif // !ANTMESSAGE_H
