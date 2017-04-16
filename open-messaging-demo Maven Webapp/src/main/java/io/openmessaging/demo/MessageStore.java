package io.openmessaging.demo;

import io.openmessaging.Message;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/**
 * @author XF
 * ��Ϣ����ת,�����߷�����Ϣ���ˣ������ߴӴ˻�ȡ��Ϣ���൱��server <p>
 * ͨ��getInstance��ȡʵ��������ģʽ.<p>
 * ���� put/pullMessage �ķ���
 */
public class MessageStore {

    private static final MessageStore INSTANCE = new MessageStore();

    public static MessageStore getInstance() {
        return INSTANCE;
    }

    /**
     * ��ϢͰ<p>
     * key:bucket(topic����queue)  --  value:��Ӧ��message list.<p>
     * (ʹ��ArrayList����Ϊ��ƫ��ȡ����Ҫ����)
     */
    private Map<String, ArrayList<Message>> messageBuckets = new HashMap<>();
    
    /**
     * ������¼��ǰqueue�����ѵ�������<p>
     * key:queue  --  value: HashMap< bucket, offset> <p>
     * ��������bucket����topic��
     */
    private Map<String, HashMap<String, Integer>> queueOffsets = new HashMap<>();

    /**
     * ��message������Ӧbucket(topic/queue)��list�У�ͬ��������<p>
     * ��ÿ��queue����topic����һ��list�������message��
     * @param bucket topic/queue
     * @param message
     */
    public synchronized void putMessage(String bucket, Message message) {
        if (!messageBuckets.containsKey(bucket)) {
            messageBuckets.put(bucket, new ArrayList<>(1024));
        }
        ArrayList<Message> bucketList = messageBuckets.get(bucket);
        bucketList.add(message);
    }
    /**
     * ͨ��queue��bucket��ȡ��һ��Ҫ���ѵ�message��
     * <p>��ȡbucket��һ��message��ƫ��offset����offset��bucketList�л�ȡmessage��
     * @param queue
     * @param bucket queue������߰󶨵�topics�е�һ����
     */
   public synchronized Message pullMessage(String queue, String bucket) {
	   //��bucket��message�б�
        ArrayList<Message> bucketList = messageBuckets.get(bucket);
        if (bucketList == null) {
            return null;
        }
        HashMap<String, Integer> offsetMap = queueOffsets.get(queue);
        if (offsetMap == null) {
            offsetMap = new HashMap<>();
            queueOffsets.put(queue, offsetMap);
        }
        int offset = offsetMap.getOrDefault(bucket, 0);
        //�����Ӧbucket��message�Ѿ�ȡ����
        if (offset >= bucketList.size()) {
            return null;
        }
        Message message = bucketList.get(offset);
        offsetMap.put(bucket, ++offset);
        return message;
   }
}
