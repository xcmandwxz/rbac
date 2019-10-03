package com.xcm.atxcm.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xcm.atxcm.bean.User;
import com.xcm.atxcm.dao.UserDao;
import com.xcm.atxcm.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserDao userDao;

	public List<User> queryAll() {
		return userDao.queryAll();
	}

	public User query4Login(User user) {
		return userDao.query4Login(user);
	}
	//返回分页数据
	public List<User> pageQueryData(Map<String, Object> map) {
		return userDao.pageQueryData(map);
	}

	public int pageQueryCount(Map<String, Object> map) {
		return userDao.pageQueryCount(map);
	}

	public void insertUser(User user) {
		userDao.insertUser(user);
	}

	public User queryById(Integer id) {
		return userDao.queryById(id);
	}

	public void updateUser(User user) {
		userDao.updateUser(user);
	}

	public void deleteUserById(Integer id) {
		userDao.deleteUserById(id);
	}

	public void deleteUsers(Map<String, Object> map) {
		userDao.deleteUsers(map);
	}

	public void deleteUserRoles(Map<String, Object> map) {
		userDao.deleteUserRoles(map);
	}

	public void insertUserRoles(Map<String, Object> map) {
		userDao.insertUserRoles(map);
	}

	public List<Integer> queryRoleidsByUserid(Integer id) {
		return userDao.queryRoleidsByUserid(id);
	}
}
