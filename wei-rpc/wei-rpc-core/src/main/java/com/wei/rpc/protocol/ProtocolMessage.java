package com.wei.rpc.protocol;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProtocolMessage<T> {

    /**
     * 消息头
     */
    private Header header;
    /**
     * 消息体
     */
    private T body;



    @Data
    public static class Header{
        /**
         * 魔数
         */
        private byte magic;

        /**
         * 版本号
         */
        private byte version;

        /**
         * 序列化器
         */
        private byte serializer;

        /**
         * 消息类型（请求与响应）
         */
        private byte type;

        private byte status;
        /**
         * 状态
         */
        private long requestId;

        /**
         * 消息体长度
         */
        private int bodyLength;




    }


}
