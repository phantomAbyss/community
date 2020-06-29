package com.wy.community.controller;

import com.wy.community.entity.Comment;
import com.wy.community.service.CommentService;
import com.wy.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping(path = "/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path = "/add/{postId}", method = RequestMethod.POST)
    public String addComment(@PathVariable("postId") int postId, Comment comment){
        comment.setCreateTime(new Date());
        comment.setStatus(0);;
        comment.setUserId(hostHolder.getUser().getId());
        commentService.addComment(comment);
        return "redirect:/discuss/detail/" + postId;
    }

}
