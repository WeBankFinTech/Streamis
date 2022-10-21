package com.webank.wedatasphere.streamis.jobmanager.log.collector.sender.buf;

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
    @SuppressWarnings("all")
    public int writeBuf(Object[] elements, int srcIndex, int length) {
        if (srcIndex < elements.length){
            int startPos = nextPosition(Math.min(elements.length - srcIndex, length), Flag.WRITE_MODE);
            if (startPos >= 0){
                int writes = position() - startPos;
                System.arraycopy(elements, srcIndex, this.buf, startPos, writes);
                return writes;
            }
        }
        return -1;
    }

    @Override
    @SuppressWarnings("all")
    public int readBuf(Object[] elements, int srcIndex, int length) {
        if (srcIndex < elements.length){
            int startPos = nextPosition(Math.min(elements.length - srcIndex, length), Flag.READ_MODE);
            if (startPos >= 0){
                int reads = position() - startPos;
                System.arraycopy(this.buf, startPos, elements, srcIndex, reads);
                return reads;
            }
        }
        return -1;
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
    @SuppressWarnings("unchecked")
    public E readBuf() {
        int startPos = nextPosition(1, Flag.READ_MODE);
        if (startPos >= 0){
            return (E)buf[startPos];
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public SendBuffer<E> compact(Function<E, Boolean> dropAble) {
        checkFlag(Flag.READ_MODE);
        int offset = 0;
        int compact = position() - 1;
        for(int i = position(); i < capacity; i ++){
           Object element = buf[i];
           if (dropAble.apply((E)element)){
               buf[i] = null;
               offset ++;
           } else {
               compact = i - offset;
               buf[compact] = element;
           }
        }
        position(compact + 1);
        limit(this.capacity);
        setFlag(Flag.WRITE_MODE);
        return this;
    }

}
