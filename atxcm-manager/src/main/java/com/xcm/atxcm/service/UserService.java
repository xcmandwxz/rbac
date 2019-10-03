package com.xcm.atxcm.service;

import java.util.List;
import java.util.Map;

import com.xcm.atxcm.bean.User;

public interface UserService {
	
	//返回所有用户
	List<User> queryAll();

	User query4Login(User user);

	List<User> pageQueryData(Map<String, Object> map);

	int pageQueryCount(Map<String, Object> map);

	void insertUser(User user);

	User queryById(Integer id);

	void updateUser(User user);

	void deleteUserById(Integer id);

	void deleteUsers(Map<String, Object> map);

	void deleteUserRoles(Map<String, Object> map);

	void insertUserRoles(Map<String, Object> map);

	List<Integer> queryRoleidsByUserid(Integer id);

}
