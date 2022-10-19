package com.webank.wedatasphere.streamis.jobmanager.log.collector.sender;

import java.util.Arrays;
import java.util.function.Function;

/**
 * Immutable send buffer (use array)
 */
public class ImmutableSendBuffer<E> extends AbstractSendBuffer<E>{

    /**
     * Buffer object array
     */
    private final Object[] buf;

    public ImmutableSendBuffer(int capacity) {
        super(capacity);
        buf = new Object[capacity];
    }

    @Override
    protected void clearBuf() {
        // Release the memory occupied
        Arrays.fill(buf, null);
    }

    @Override
    public void capacity(String newCapacity) {
        throw new IllegalArgumentException("Unsupported to scale-in/scale-up the send buffer");
    }

    @Override
    public int writeBuf(E[] elements, int srcIndex, int length) {
        if (srcIndex < elements.length){
            int startPos = nextPosition(Math.min(elements.length - srcIndex, length), Flag.WRITE_MODE);
            if (startPos >= 0){
                int writes = position() - startPos;
                for (int i = srcIndex; i < writes; i ++){
                    buf[startPos++] = elements[i];
                }
                return writes;
            }
        }
        return -1;
    }

    @Override
    public int readBuf(E[] elements, int srcIndex, int length) {
        return 0;
    }

    @Override
    public int writeBuf(E element) {
        int startPos = nextPosition(1, Flag.WRITE_MODE);
        if (startPos >= 0){
            buf[startPos] = element;
            return 1;
        }
        return -1;
    }

    @Override
    public E readBuf() {
        return null;
    }

    @Override
    public SendBuffer<E> compact(Function<E, Boolean> dropAble) {
        return null;
    }
}
