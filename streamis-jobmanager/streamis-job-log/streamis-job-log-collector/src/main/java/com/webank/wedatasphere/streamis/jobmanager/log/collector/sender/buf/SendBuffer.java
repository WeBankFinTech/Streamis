package com.webank.wedatasphere.streamis.jobmanager.log.collector.sender.buf;

import java.util.function.Function;

/**
 * Buffer for Rpc sender
 * @param <E> buffer element
 */
public interface SendBuffer<E> {

    /**
     * Capacity
     * @return int
     */
    int capacity();

    /**
     * Is read mode
     * @return boolean
     */
    boolean isReadMode();

    /**
     * Is write mode
     * @return boolean
     */
    boolean isWriteMode();
    /**
     * Scale-up or scale-in
     * @param newCapacity  new capacity
     */
    void capacity(String newCapacity);
    /**
     * Remain size
     * (remain space for writing or remain elements for reading)
     * @return int
     */
    int remaining();

    /**
     * Transient between write-mode and read-mode
     */
    void flip();

    /**
     * Restart from the beginning of window
     */
    void rewind();
    /**
     * Clear to reuse the buffer
     */
    void clear();
    /**
     * Write buffer element
     * @param element element
     * @return if succeed
     */
    int writeBuf(E element);

    /**
     * Write buffer element array
     * @param elements elements
     * @param srcIndex the src index in elements
     * @param length the length to read
     * @return write num
     */
    int writeBuf(E[] elements, int srcIndex, int length);

    /**
     * Read buffer element
     * @return element
     */
    E readBuf();

    /**
     * Read buffer element array
     * @param elements elements
     * @param srcIndex the src index in elements
     * @param length the length to write
     * @return read num
     */
    int readBuf(E[] elements, int srcIndex, int length);

    /**
     * Compact the buffer, avoid the useless elements
     * @param dropAble drop function
     * @return send buffer
     */
    SendBuffer<E> compact(Function<E, Boolean> dropAble);


}
