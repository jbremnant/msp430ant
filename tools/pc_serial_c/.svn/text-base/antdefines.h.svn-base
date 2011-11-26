/* ANT PROTOCOL LIBRARY INTERFACE - DO NOT MODIFY!
 *
 * Dynastream Innovations Inc.
 * Cochrane, AB, CANADA
 *
 * Copyright (c) 2008 Dynastream Innovations Inc.
 * THIS SOFTWARE IS THE INTERFACE TO THE ANT PROTOCOL MODULE
 * IT MAY BE USED, MODIFIED and DISTRIBUTED ONLY IN ACCORDANCE TO THE 
 * APPROPRIATE LICENCING AGREEMENT
 */ 

#ifndef ANTDEFINES_H
#define ANTDEFINES_H

#include "types.h"

//////////////////////////////////////////////
// Clock Definition
//////////////////////////////////////////////
#define CLOCK_FREQUENCY                            ((ULONG)32768)          // ANT system clock frequency

//////////////////////////////////////////////
// Radio TX Power Definitions
//////////////////////////////////////////////
#define RADIO_TX_POWER_MASK                        ((UCHAR)0x03)
#define RADIO_TX_POWER_MINUS20DB                   ((UCHAR)0x00)
#define RADIO_TX_POWER_MINUS10DB                   ((UCHAR)0x01)
#define RADIO_TX_POWER_MINUS5DB                    ((UCHAR)0x02)
#define RADIO_TX_POWER_0DB                         ((UCHAR)0x03)

//////////////////////////////////////////////
// Default System Definitions
//////////////////////////////////////////////
#define DEFAULT_CHANNEL_MESSAGE_FREQUENCY          ((ULONG)4)
#define DEFAULT_CHANNEL_MESSAGE_PERIOD             ((USHORT)(CLOCK_FREQUENCY / DEFAULT_CHANNEL_MESSAGE_FREQUENCY)) // 8192
#define DEFAULT_RADIO_TX_POWER                     RADIO_TX_POWER_0DB      // ANT default RF power
#define DEFAULT_RADIO_CHANNEL                      ((UCHAR)66)             // 2400MHz + 1MHz * Channel Number = 2466MHz
#define DEFAULT_RX_SEARCH_TIMEOUT                  ((UCHAR)12)             // 12 * 2.5 seconds = 30 seconds

//////////////////////////////////////////////
// ID Definitions
//////////////////////////////////////////////
#define ANT_ID_SIZE                                ((UCHAR)4)
#define ANT_ID_TRANS_TYPE_OFFSET                   ((UCHAR)3)
#define ANT_ID_DEVICE_TYPE_OFFSET                  ((UCHAR)2)
#define ANT_ID_DEVICE_NUMBER_HIGH_OFFSET           ((UCHAR)1)
#define ANT_ID_DEVICE_NUMBER_LOW_OFFSET            ((UCHAR)0)
#define ANT_ID_DEVICE_TYPE_PAIRING_FLAG            ((UCHAR)0x80)

//////////////////////////////////////////////
// Assign Channel Parameters
//////////////////////////////////////////////
#define PARAMETER_TX_NOT_RX                        ((UCHAR)0x10)
#define PARAMETER_SHARED_CHANNEL                   ((UCHAR)0x20)  
#define PARAMETER_NO_TX_GUARD_BAND                 ((UCHAR)0x40)   
#define PARAMETER_ALWAYS_RX_WILD_CARD_SEARCH_ID    ((UCHAR)0x40)
#define PARAMETER_RSSI_CHANNEL                     ((UCHAR)0x80)

//////////////////////////////////////////////
// Channel Status
//////////////////////////////////////////////
#define STATUS_CHANNEL_STATE_MASK                  ((UCHAR)0x03)
#define STATUS_UNASSIGNED_CHANNEL                  ((UCHAR)0x00)
#define STATUS_ASSIGNED_CHANNEL                    ((UCHAR)0x01)
#define STATUS_SEARCHING_CHANNEL                   ((UCHAR)0x02)
#define STATUS_TRACKING_CHANNEL                    ((UCHAR)0x03)

//////////////////////////////////////////////
// Standard capabilities defines
//////////////////////////////////////////////
#define CAPABILITIES_NO_RX_CHANNELS                ((UCHAR)0x01)
#define CAPABILITIES_NO_TX_CHANNELS                ((UCHAR)0x02)
#define CAPABILITIES_NO_RX_MESSAGES                ((UCHAR)0x04)
#define CAPABILITIES_NO_TX_MESSAGES                ((UCHAR)0x08)
#define CAPABILITIES_NO_ACKD_MESSAGES              ((UCHAR)0x10)
#define CAPABILITIES_NO_BURST_TRANSFER             ((UCHAR)0x20)

//////////////////////////////////////////////
// Advanced capabilities defines
//////////////////////////////////////////////
#define CAPABILITIES_OVERUN_UNDERRUN               ((UCHAR)0x01)     // Support for this functionality has been dropped
#define CAPABILITIES_NETWORK_ENABLED               ((UCHAR)0x02)
#define CAPABILITIES_AP1_VERSION_2                 ((UCHAR)0x04)     // This Version of the AP1 does not support transmit and only had a limited release
#define CAPABILITIES_SERIAL_NUMBER_ENABLED         ((UCHAR)0x08)
#define CAPABILITIES_PER_CHANNEL_TX_POWER_ENABLED  ((UCHAR)0x10)
#define CAPABILITIES_LOW_PRIORITY_SEARCH_ENABLED   ((UCHAR)0x20)
#define CAPABILITIES_SENSRCORE_ENABLED             ((UCHAR)0x40)
#define CAPABILITIES_SEARCH_LIST_ENABLED           ((UCHAR)0x80)

//////////////////////////////////////////////
// Advanced capabilities 2 defines
//////////////////////////////////////////////
#define CAPABILITIES_LED_ENABLED                   ((UCHAR)0x01)
#define CAPABILITIES_EXT_MESSAGE_ENABLED           ((UCHAR)0x02)
#define CAPABILITIES_SCAN_CHANNEL_ENABLED          ((UCHAR)0x04)
#define CAPABILITIES_TX_SEARCH_CMD_ENABLED         ((UCHAR)0x08)
// bits 4-7 reserved

//////////////////////////////////////////////
// Burst Message Sequence 
//////////////////////////////////////////////
#define CHANNEL_NUMBER_MASK                        ((UCHAR)0x1F)
#define SEQUENCE_NUMBER_MASK                       ((UCHAR)0xE0)
#define SEQUENCE_NUMBER_INC                        ((UCHAR)0x20)
#define SEQUENCE_NUMBER_ROLLOVER                   ((UCHAR)0x60)
#define SEQUENCE_LAST_MESSAGE                      ((UCHAR)0x80)

//////////////////////////////////////////////
// Response / Event Codes
//////////////////////////////////////////////
#define RESPONSE_NO_ERROR                          ((UCHAR)0x00)

#define EVENT_RX_SEARCH_TIMEOUT                    ((UCHAR)0x01)
#define EVENT_RX_FAIL                              ((UCHAR)0x02)
#define EVENT_TX                                   ((UCHAR)0x03)
#define EVENT_TRANSFER_RX_FAILED                   ((UCHAR)0x04)
#define EVENT_TRANSFER_TX_COMPLETED                ((UCHAR)0x05)
#define EVENT_TRANSFER_TX_FAILED                   ((UCHAR)0x06)
#define EVENT_CHANNEL_CLOSED                       ((UCHAR)0x07)
#define EVENT_RX_FAIL_GO_TO_SEARCH                 ((UCHAR)0x08)
#define EVENT_CHANNEL_COLLISION                    ((UCHAR)0x09)
#define EVENT_TRANSFER_TX_START                    ((UCHAR)0x0A)           // a pending transmit transfer has begun

#define EVENT_TRANSFER_TX_COMPLETED_RSSI           ((UCHAR)0x10)

#define CHANNEL_IN_WRONG_STATE                     ((UCHAR)0x15)           // returned on attempt to perform an action from the wrong channel state
#define CHANNEL_NOT_OPENED                         ((UCHAR)0x16)           // returned on attempt to communicate on a channel that is not open
#define CHANNEL_ID_NOT_SET                         ((UCHAR)0x18)           // returned on attempt to open a channel without setting the channel ID
#define CLOSE_ALL_CHANNELS                         ((UCHAR)0x19)           // returned when attempting to start scanning mode, when channels are still open

#define TRANSFER_IN_PROGRESS                       ((UCHAR)0x1F)           // returned on attempt to communicate on a channel with a TX transfer in progress
#define TRANSFER_SEQUENCE_NUMBER_ERROR             ((UCHAR)0x20)           // returned when sequence number is out of order on a Burst transfer
#define TRANSFER_IN_ERROR                          ((UCHAR)0x21)
#define TRANSFER_BUSY                              ((UCHAR)0x22) 

#define INVALID_MESSAGE                            ((UCHAR)0x28)           // returned when the message has an invalid parameter
#define INVALID_NETWORK_NUMBER                     ((UCHAR)0x29)           // returned when an invalid network number is provided
#define INVALID_LIST_ID                            ((UCHAR)0x30)           // returned when the provided list ID or size exceeds the limit
#define INVALID_SCAN_TX_CHANNEL                    ((UCHAR)0x31)           // returned when attempting to transmit on channel 0 when in scan mode

#define NVM_FULL_ERROR                             ((UCHAR)0x40)           // error writing to NVM, memory is full
#define NVM_WRITE_ERROR                            ((UCHAR)0x41)           // error writing to NVM, bytes not written correctly

#define NO_RESPONSE_MESSAGE                        ((UCHAR)0x50)           // returned to the Command_SerialMessageProcess function, so no reply message is generated
#define RETURN_TO_MFG                              ((UCHAR)0x51)           // default return to any mesg when the module determines that the mfg procedure has not been fully completed

//////////////////////////////////////////////
// PC Application Event Codes
//////////////////////////////////////////////
//NOTE: These events are not generated by the embedded ANT module

#define EVENT_RX_BROADCAST                         ((UCHAR)0x9A)           // returned when module receives broadcast data
#define EVENT_RX_ACKNOWLEDGED                      ((UCHAR)0x9B)           // returned when module receives acknowledged data
#define EVENT_RX_BURST_PACKET                      ((UCHAR)0x9C)           // returned when module receives burst data

#define EVENT_RX_EXT_BROADCAST                     ((UCHAR)0x9D)           // returned when module receives broadcast data
#define EVENT_RX_EXT_ACKNOWLEDGED                  ((UCHAR)0x9E)           // returned when module receives acknowledged data
#define EVENT_RX_EXT_BURST_PACKET                  ((UCHAR)0x9F)           // returned when module receives burst data

#define EVENT_RX_RSSI_BROADCAST                    ((UCHAR)0xA0)           // returned when module receives broadcast data
#define EVENT_RX_RSSI_ACKNOWLEDGED                 ((UCHAR)0xA1)           // returned when module receives acknowledged data
#define EVENT_RX_RSSI_BURST_PACKET                 ((UCHAR)0xA2)           // returned when module receives burst data

#define EVENT_RX_BTH_BROADCAST                     ((UCHAR)0xA3)           // returned when module receives broadcast data
#define EVENT_RX_BTH_ACKNOWLEDGED                  ((UCHAR)0xA4)           // returned when module receives acknowledged data
#define EVENT_RX_BTH_BURST_PACKET                  ((UCHAR)0xA5)           // returned when module receives burst data

#define EVENT_RX_BTH_EXT_BROADCAST                 ((UCHAR)0xA6)           // returned when module receives broadcast data
#define EVENT_RX_BTH_EXT_ACKNOWLEDGED              ((UCHAR)0xA7)           // returned when module receives acknowledged data
#define EVENT_RX_BTH_EXT_BURST_PACKET              ((UCHAR)0xA8)           // returned when module receives burst data
//////////////////////////////////////////////
// NVM Command Codes
//////////////////////////////////////////////

#define NVM_CMD_FORMAT                             ((UCHAR)0x00)
#define NVM_CMD_DUMP                               ((UCHAR)0x01)
#define NVM_CMD_SET_DEFAULT_SECTOR                 ((UCHAR)0x02)
#define NVM_CMD_END_SECTOR                         ((UCHAR)0x03)                 
#define NVM_CMD_END_DUMP                           ((UCHAR)0x04)
#define NVM_CMD_LOCK_NVM                           ((UCHAR)0x05)

#endif // !ANTDEFINES_H                                                    


