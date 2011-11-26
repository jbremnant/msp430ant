#!/usr/bin/env python
import sys, time, operator, struct, array
from twisted.internet import defer, protocol, reactor
from twisted.internet.serialport import SerialPort

def hexList(data):
    return map(lambda s: s.encode('HEX'), data)

def hexRepr(data):
    return repr(hexList(data))

def intListToByteList(data):
    return map(lambda i: struct.pack('!H', i)[1], array.array('B', data))

class ANT(protocol.Protocol):

    def __init__(self, chan=0x00, interMessageDelay=.001, messageDelay=.05, debug=False):
        self._interMessageDelay = interMessageDelay
        self._messageDelay = messageDelay
        self._debug = debug
        self._chan = chan

        self._state = 0
        self._transmitBuffer = []
        self._receiveBuffer = []


    def dataReceived(self, data):
        if self._debug:
            print "<-- " + hexRepr(data)

        self._receiveBuffer.extend(list(struct.unpack('%sB' % len(data), data)))

        if len(self._receiveBuffer) > 1 and self._receiveBuffer[0] == 0xa4:
            messageSize = self._receiveBuffer[1]
            totalMessageSize = messageSize + 4

            if len(self._receiveBuffer) >= totalMessageSize:
                message = self._receiveBuffer[:totalMessageSize]
                self._receiveBuffer = self._receiveBuffer[totalMessageSize:]

                if reduce(operator.xor, message[:-1]) != message[-1]:
                    print "RCV CORRUPT MSG: %s" % hexRepr(intListToByteList(message))
                    return

                printBuffer = []
                if message[2] == 0x40:
                    printBuffer.append("MSG_RESPONSE_EVENT")
                    printBuffer.append("CHAN:%02x" % message[3])
                    printBuffer.append(self._eventToString(message[4]))
                    printBuffer.extend(hexList(intListToByteList(message[5:-1])))
                    printBuffer.append("CHKSUM:%02x" % message[-1])
                    if message[4] == 1:
                        reactor.callLater(self._messageDelay, self.opench)
                else:
                    printBuffer = hexRepr(intListToByteList(message))

                print "<-- " + repr(printBuffer)
                    
    def _eventToString(self, event):
        try:
            return { 0:"RESPONSE_NO_ERROR",
                     1:"EVENT_RX_SEARCH_TIMEOUT",
                     2:"EVENT_RX_FAIL",
                     3:"EVENT_TX",
                     4:"EVENT_TRANSFER_RX_FAILED",
                     5:"EVENT_TRANSFER_TX_COMPLETED",
                     6:"EVENT_TRANSFER_TX_FAILED",
                     7:"EVENT_CHANNEL_CLOSED",
                     8:"EVENT_RX_FAIL_GO_TO_SEARCH",
                     9:"EVENT_CHANNEL_COLLISION",
                     10:"EVENT_TRANSFER_TX_START",
                     21:"CHANNEL_IN_WRONG_STATE",
                     22:"CHANNEL_NOT_OPENED",
                     24:"CHANNEL_ID_NOT_SET",
                     25:"CLOSE_ALL_CHANNELS",
                     31:"TRANSFER_IN_PROGRESS",
                     32:"TRANSFER_SEQUENCE_NUMBER_ERROR",
                     33:"TRANSFER_IN_ERROR",
                     40:"INVALID_MESSAGE",
                     41:"INVALID_NETWORK_NUMBER",
                     48:"INVALID_LIST_ID",
                     49:"INVALID_SCAN_TX_CHANNEL",
                     51:"INVALID_PARAMETER_PROVIDED",
                     53:"EVENT_QUE_OVERFLOW",
                     64:"NVM_FULL_ERROR",
                     65:"NVM_WRITE_ERROR",
                     66:"ASSIGN_CHANNEL_ID",
                     81:"SET_CHANNEL_ID",
                     0x4b:"OPEN_CHANNEL"}[event]
        except:
            return "%02x" % event
                    
            
    def connectionLost(self, reason):
        print "connection lost: ", reason

    def reset(self):
        self._sendMessage(0x4a, 0x00)

    def setrf(self):
        self._sendMessage(0x45, 0x00, 0x39)

    def setchperiod(self):
        self._sendMessage(0x43, 0x1f, 0x86)

    def setchid(self):
        self._sendMessage(0x51, 0x00, 0x00, 0x00, 0x00, 0x00)

    def opench(self):
        self._sendMessage(0x4b, 0x00)

    def assignch(self):
        self._sendMessage(0x42, self._chan, 0x00, 0x00)

    def sendStr(self, instring):
        if len(instring) > 8:
            raise "string is too big"

        self._sendMessage(*[0x4e] + list(struct.unpack('%sB' % len(instring), instring)))
        
    def _sendMessage(self, *args):
        data = list(args)
        data.insert(0, len(data) - 1)
        data.insert(0, 0xa4)
        self._transmitBuffer = map(lambda i: struct.pack('!H', i)[1], array.array('B', data + [reduce(operator.xor, data)]))

        if self._debug:
            print "--> " + hexRepr(self._transmitBuffer)

        reactor.callLater(0, self._send)

    def _send(self):
        if len(self._transmitBuffer) == 0:
            reactor.callLater(self._messageDelay, self.next)

        else:
            b = self._transmitBuffer.pop(0)
            self.transport.write(b)
            reactor.callLater(self._interMessageDelay, self._send)

    def connectionMade(self):
        reactor.callLater(self._messageDelay, self.next)

    def next(self):
        if self._state == 0:
            self.reset()

        elif self._state == 1:
            self.assignch()

        elif self._state == 2:
            self.setrf()

        elif self._state == 3:
            self.setchperiod()

        elif self._state == 4:
            self.setchid()

        elif self._state == 5:
            self.opench()

        elif self._state == 6:
            pass #self.sendStr('aaaaaaaa')
            
        if self._state < 6:
            self._state += 1


proto = ANT(debug=False)
if len(sys.argv) < 2:
    print "useage: %s COM_PORT_NUMBER_OR_SERIAL_DEVICE" % sys.argv[0]
    sys.exit(1)

port = SerialPort(proto, sys.argv[1], reactor)
port.setBaudRate(4800)
reactor.run()
