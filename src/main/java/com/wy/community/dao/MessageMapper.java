package com.wy.community.dao;

import com.wy.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {

    //查询当前的会话列表，针对每个会话只返回最新的一条最新的私信
    List<Message> selectConversations(int userId, int offset, int limit);

    //查询当前用户的会话数
    int selectConversationCount(int userId);

    //查询每个会话包含的私信列表
    List<Message> selectLetters(String conversationId, int offset, int limit);

    //查询每个会话的所包含的私信数量
    int selectLetterCount(String conversationId);

    //查询未读的私信数量
    int selectLetterUnReadCount(int userId, String conversationId);

    //新增一条信息
    int insertMessage(Message message);

    //修改消息状态
    int updateStatus(List<Integer> ids, int status);

}
