package io.openmessaging.demo;

import io.openmessaging.KeyValue;
import io.openmessaging.Message;
import io.openmessaging.PullConsumer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;


/**
 * @author XF
 *����ȡ�����ߵ�Ĭ��ʵ�֡���ʵ�������<p>
 * ��Ҫ������������1����queue��topic 2����ȡ��һ��message����
 */
public class DefaultPullConsumer implements PullConsumer {
    private MessageStore messageStore = MessageStore.getInstance();
    private KeyValue properties;
    //����ǰ���������ѵ�queue
    private String queue;
    private Set<String> buckets = new HashSet<>();
    //��Ӧ������queue��topics
    private List<String> bucketList = new ArrayList<>();

    private int lastIndex = 0;

    public DefaultPullConsumer(KeyValue properties) {
        this.properties = properties;
    }


    @Override public KeyValue properties() {
        return properties;
    }

    @Override public Message pull() {

        throw new UnsupportedOperationException("Unsupported");
    }

    @Override public Message pull(KeyValue properties) {
        throw new UnsupportedOperationException("Unsupported");
    }

    @Override public Message pull(long timeout, TimeUnit unit) {
        throw new UnsupportedOperationException("Unsupported");
    }

    @Override public Message pull(long timeout, TimeUnit unit, KeyValue properties) {
        throw new UnsupportedOperationException("Unsupported");
    }

    /**
     * ��ѯ��ȡ��һ����Ϣ���� ��ֱ����queue�����󶨵�topic��������(ͬ�����������ö���) <p>
     * ��ѯ��ÿ����ȡ��ͬqueue/topic����һ��message<p>
     * queue�����ڶ�������ԣ���Ҫͨ������attachQueue��á�
     */
    @Override public synchronized Message pullNoWait() {
        if (buckets.size() == 0 || queue == null) {
            return null;
        }
        //use Round Robin ѭ��
        int checkNum = 0;
        while (++checkNum <= bucketList.size()) {
        	
        	//���±�lastIndex+1��ʼ����bucketList��ȡ��һ��bucket(��ѯ����һ�Σ�Ȼ����һ��)
            String bucket = bucketList.get((++lastIndex) % (bucketList.size()));
            
            //��bucket��queue��messageStoreȡ��һ����Ϊnull��message
            Message message = messageStore.pullMessage(queue, bucket);
            if (message != null) {
                return message;
            }
        }
        return null;
    }

    @Override public Message pullNoWait(KeyValue properties) {
        throw new UnsupportedOperationException("Unsupported");
    }

    /**
     * Ϊ������ѡ��queue�����Ҷ����ض���topics(ͬ�����������ö���)<p>
     * ����һ�α������������߶�����һ��queue����Ӧ��topics<p>
     * <p>
     * buckets��set,����bucketList˳�򲻹̶�������û���ظ���
     */
    @Override public synchronized void attachQueue(String queueName, Collection<String> topics) {
    	//queue���ܵ���queueName �����˵���Ѿ����ڸ�queue���ˡ�
        if (queue != null && !queue.equals(queueName)) {
            throw new ClientOMSException("You have alreadly attached to a queue " + queue);
        }
        queue = queueName;
        
        //��queueName��topics����ӵ�buckets.buckets�о�Ӧ����գ���Ϊ��ȡʱ����ͬqueue��message�϶���������
        buckets.add(queueName);
        buckets.addAll(topics);
        bucketList.clear();
        bucketList.addAll(buckets);
    }

    @Override public void start() {

    }

    @Override public void shutdown() {

    }

}
