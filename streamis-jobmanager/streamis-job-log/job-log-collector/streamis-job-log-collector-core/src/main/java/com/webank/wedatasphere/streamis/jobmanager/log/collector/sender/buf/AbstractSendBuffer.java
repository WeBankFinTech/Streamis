package com.webank.wedatasphere.streamis.jobmanager.log.collector.sender.buf;

import java.util.function.Predicate;

/**
 * Abstract sender buffer;
 * non-blocking and reduces out-of-bounds exceptions
 */
public abstract class AbstractSendBuffer<E> implements SendBuffer<E>{

    @SuppressWarnings("unchecked")
    public abstract SendBuffer<E> compact(Predicate<E> dropAble);

    protected enum Flag{
        WRITE_MODE, READ_MODE
    }

    /**
     * Access flag
     */
    private Flag accessFlag = Flag.WRITE_MODE;

    private int position = 0;
    private int limit;
    /**
     * The capacity is mutable
     */
    protected int capacity;


    protected AbstractSendBuffer(int capacity){
        this.capacity = capacity;
        limit(this.capacity);
    }

    protected AbstractSendBuffer(){
        this(Integer.MAX_VALUE);
    }

    @Override
    public boolean isReadMode() {
        return accessFlag == Flag.READ_MODE;
    }

    @Override
    public boolean isWriteMode() {
        return accessFlag == Flag.WRITE_MODE;
    }

    @Override
    public int capacity() {
        return this.capacity;
    }

    @Override
    public int remaining() {
        int rem = this.limit - this.position;
        return Math.max(rem, 0);
    }

    @Override
    public void flip() {
        checkFlag(Flag.WRITE_MODE);
        this.limit = this.position;
        this.position = 0;
        this.accessFlag = Flag.READ_MODE;
    }

    @Override
    public void rewind() {
        position = 0;
    }

    @Override
    public void clear() {
        limit(this.capacity);
        this.position = 0;
        this.accessFlag = Flag.WRITE_MODE;
        clearBuf();
    }

    /**
     * Change the limit value
     * @param newLimit new limit
     */
    final void limit(int newLimit){
        if (newLimit > this.capacity || (newLimit < 0)){
            throw new IllegalArgumentException("Set the illegal limit value: " + newLimit + " in send buffer, [capacity: " + this.capacity + "]");
        }
        this.limit = newLimit;
        if (this.position > newLimit){
            this.position = newLimit;
        }
    }

    /**
     * Inc the position with offset
     * @param offset offset value
     * @param accessFlag access flag
     * @return the current position value
     */
    final int nextPosition(int offset, Flag accessFlag){
        checkFlag(accessFlag);
        int p = position;
        // Reach the limit, return -1 value
        if (p >= limit){
            return -1;
        }
        if (p + offset > limit){
            this.position = limit;
        } else {
            this.position = p + offset;
        }
        return p;
    }

    final void checkFlag(Flag accessFlag){
        if (this.accessFlag != accessFlag){
            throw new IllegalStateException("Illegal access flag [" + accessFlag + "] for send buffer");
        }
    }
    final void setFlag(Flag accessFlag){
        this.accessFlag = accessFlag;
    }
    /**
     *
     * @return the current position
     */
    final int position(){
        return this.position;
    }

    final void position(int position){
        this.position = position;
    }
    /**
     * Do the actual clear
     */
    protected abstract void clearBuf();
}
