package com.mashibing.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashibing.entity.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.hash.Jackson2HashMapper;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TestRedis {
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    @Qualifier("ooxx")
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    ObjectMapper objectMapper;

    public void testRedis(){
//        redisTemplate.opsForValue().set("hello", "hello koukay");
        //高级api
       /* stringRedisTemplate.opsForValue().set("hello02", "hello koukay");
        System.out.println(stringRedisTemplate.opsForValue().get("hello02"));*/
       //低级api
        RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
        connection.set("hello03".getBytes(), "123".getBytes());
        System.out.println(new String(connection.get("hello03".getBytes())));

       /* HashOperations<String, Object, Object> hash = stringRedisTemplate.opsForHash();
        hash.put("sean", "name", "houkai");
        hash.put("sean", "age", "18");
        System.out.println(hash.entries("sean"));*/

        Person p = new Person();
        p.setName("jack");
        p.setAge(18);


        /*Jackson2HashMapper jm = new Jackson2HashMapper(objectMapper, false);
        redisTemplate.opsForHash().putAll("sean01" , jm.toHash(p));
        Map map = redisTemplate.opsForHash().entries("sean01");
        Person person = objectMapper.convertValue(map, Person.class);
        System.out.println(person);
        System.out.println(person.getName()+"<==>"+p.getAge());*/

//        stringRedisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<Object>(Object.class));
        Jackson2HashMapper jm = new Jackson2HashMapper(objectMapper, false);
        stringRedisTemplate.opsForHash().putAll("sean02" , jm.toHash(p));
        Map map = stringRedisTemplate.opsForHash().entries("sean02");
        Person person = objectMapper.convertValue(map, Person.class);
        System.out.println(person);
        System.out.println(person.getName()+"<==>"+p.getAge());




        //获得连接
        RedisConnection cc = stringRedisTemplate.getConnectionFactory().getConnection();
        //订阅消息
        cc.subscribe(new MessageListener() {
            @Override
            public void onMessage(Message message, byte[] bytes) {
                byte[] body = message.getBody();
                System.out.println(new String(body));
            }
        }, "ooxx".getBytes());
        while (true){
            //发送消息
            stringRedisTemplate.convertAndSend("ooxx", "hello");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
