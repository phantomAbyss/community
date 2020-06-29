package com.wy.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    //日志记录
    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    //根节点
    private TrieNode rootNode = new TrieNode();

    //替换敏感词
    private static final String REPLACEMENT = "***";

    @PostConstruct
    public void init(){
        try (
                //读取敏感词文件
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                //构建字符流
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                ) {
            String keyWord;
            while((keyWord = br.readLine()) != null){
                //构建前缀树
                this.addKeyWord(keyWord);
            }
        } catch (IOException e) {
            logger.error("敏感词文件读取失败：" + e.getMessage());
        }

    }

    /**
     * 过滤敏感词
     * @param text  待过滤的文本
     * @return
     */
    public String filter(String text){
        if(StringUtils.isBlank(text))
            return null;
        //前缀树的指针
        TrieNode tempNode = rootNode;
        //指针1
        int begin = 0;
        int postion = 0;
        //存储过滤结果
        StringBuilder res = new StringBuilder();
        while(postion < text.length()){
            char ch = text.charAt(postion);

            //跳过符号
            if(isSymbol(ch)){
                //如果此时前缀树指针指向开头，则将该符号接入结果
                if(tempNode == rootNode){
                    res.append(ch);
                    begin++;
                }
                //物流在开头还是中间，postion都需要右移
                postion++;
                continue;
            }

            tempNode = tempNode.getSubNode(ch);
            if(tempNode == null){
                //begin开头的字符串不是敏感词
                res.append(text.charAt(begin));
                //从开始位置的下一个位置进行判断
                postion = ++begin;
                tempNode = rootNode;
            }else if(tempNode.isKeyWordEnd()){
                //发现了敏感词
                res.append(REPLACEMENT);
                //从postion的下一个位置开始
                begin = ++postion;
                tempNode = rootNode;
            }else{
                postion++;
            }
        }
        //将尾部没有构成敏感词的字符串加入结果集
        res.append(text.substring(begin));
        return res.toString();
    }

    public boolean isSymbol(Character ch){
        //0x2E80~0x9FFF表示东亚字符
        return !CharUtils.isAsciiAlphanumeric(ch) && (ch < 0x2E80 || ch > 0x9FFF);
    }

    /**
     * 添加敏感词到前缀树
     * @param keyWord
     */
    public void addKeyWord(String keyWord){
        TrieNode tempNode = rootNode;
        for(int i = 0; i < keyWord.length(); i++){
            char ch = keyWord.charAt(i);
            TrieNode subNode = tempNode.getSubNode(ch);
            if(subNode == null){
                //初始化子节点
                subNode = new TrieNode();
                tempNode.addSubNode(ch, subNode);
            }
            //指向下一个添加的节点
            tempNode = subNode;

            //判断是否到达了敏感词的结尾
            if(i == keyWord.length() - 1){
                tempNode.setKeyWordEnd(true);
            }
        }
    }


    //前缀树
    private class TrieNode {

        //关键字结束符
        private boolean isKeyWordEnd = false;

        //子节点
        private Map<Character, TrieNode> subNode = new HashMap<>();

        public boolean isKeyWordEnd() {
            return isKeyWordEnd;
        }

        public void setKeyWordEnd(boolean keyWordEnd) {
            isKeyWordEnd = keyWordEnd;
        }

        //添加子节点
        public void addSubNode(Character c, TrieNode node){
            subNode.put(c, node);
        }

        //获取子节点
        public TrieNode getSubNode(Character c){
            return subNode.get(c);
        }
    }
}
