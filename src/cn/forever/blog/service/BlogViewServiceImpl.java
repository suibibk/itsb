package cn.forever.blog.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import cn.forever.dao.ObjectDao;

@Component("blogViewService")
public class BlogViewServiceImpl implements BlogViewService {
	@Resource(name="objectDao")
	private ObjectDao objectDao;
	
}
