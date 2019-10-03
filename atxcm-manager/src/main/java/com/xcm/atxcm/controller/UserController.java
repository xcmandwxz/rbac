package com.xcm.atxcm.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.impl.interceptor.RetryInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xcm.atxcm.bean.AJAXResult;
import com.xcm.atxcm.bean.Page;
import com.xcm.atxcm.bean.User;
import com.xcm.atxcm.bean.Role;
import com.xcm.atxcm.service.RoleService;
import com.xcm.atxcm.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {
		@Autowired
		private UserService userService;
		@Autowired
		private RoleService roleService;
	
	
		@RequestMapping("/add")
		public String add() {
			return "user/add";
		}
		
		
		@ResponseBody
		@RequestMapping("/update")
		public Object update( User user ) {
			AJAXResult result = new AJAXResult();//AJAXResult自己定义的一个类，用来接受状态，和数据的
			
			try {
				
				userService.updateUser(user);
				result.setSuccess(true);
			} catch ( Exception e ) {
				e.printStackTrace();
				result.setSuccess(false);
			}
			
			return result;
		}
		
		//进入编辑页面
		@RequestMapping("/edit")
		public String edit( Integer id, Model model ) {
			
			User user = userService.queryById(id);//找到用户
			model.addAttribute("user", user);//需要传给页面用户信息
			
			return "user/edit";
		}
		
		
		@ResponseBody
		@RequestMapping("/insert")
		public Object insert( User user ) {
			AJAXResult result = new AJAXResult();
			
			try {
				//SimpleDateFormat专门是用来格式化时间日期的
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				user.setCreatetime(sdf.format(new Date()));
				user.setUserpswd("123456");
				userService.insertUser(user);
				result.setSuccess(true);
			} catch ( Exception e ) {
				e.printStackTrace();
				result.setSuccess(false);
			}
			
			return result;
		}
		
		@ResponseBody
		@RequestMapping("/doAssign")
		public Object doAssign( Integer userid, Integer[] unassignroleids ) {
			AJAXResult result = new AJAXResult();
			
			try {
				// 增加关系表数据
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("userid", userid);
				map.put("roleids", unassignroleids);
				userService.insertUserRoles(map);
				result.setSuccess(true);
			} catch ( Exception e ) {
				e.printStackTrace();
				result.setSuccess(false);
			}
			
			return result;
		}
		
		
		@ResponseBody
		@RequestMapping("/dounAssign")
		public Object dounAssign( Integer userid, Integer[] assignroleids ) {
			AJAXResult result = new AJAXResult();
			
			try {
				// 删除关系表数据
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("userid", userid);
				map.put("roleids", assignroleids);
				userService.deleteUserRoles(map);
				result.setSuccess(true);
			} catch ( Exception e ) {
				e.printStackTrace();
				result.setSuccess(false);
			}
			
			return result;
		}
		
		
		
		
		
		@RequestMapping("/index")
		//用户界面需要传入第几页pageno，每页有几条
		//@RequestParam(required=false,defaultValue="1")中required是表示不是必须的参数，默认为一
		//pageno当前页
		public String index(@RequestParam(required=false,defaultValue="1")Integer pageno,@RequestParam(required=false,defaultValue="2")Integer pagesize,Model model) {
			//分页查询
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("start", (pageno-1)*pagesize);//计算起始位置
			map.put("size",pagesize);
			
			
			List<User> users = userService.pageQueryData(map);//获取了user数据
			model.addAttribute("users",users);//
			//传入当前页
			model.addAttribute("pageno",pageno);
			//最大页码（总页码）
			int totalsize=userService.pageQueryCount(map);
			
			int totalno = 0;
			if(totalsize%pagesize==0) {
				totalno = totalsize / pagesize;
				
			}else {
				totalno = totalsize / pagesize + 1;
			}
					
					
			return "user/index2";
		}
		
		@RequestMapping("/assign")
		public String assign( Integer id, Model model ) {
			
			User user = userService.queryById(id);
			model.addAttribute("user", user);
			
			List<Role> roles = roleService.queryAll();//获取全部角色信息
			
			List<Role> assingedRoles = new ArrayList<Role>();
			List<Role> unassignRoles = new ArrayList<Role>();
			
			// 获取关系表的数据
			List<Integer> roleids = userService.queryRoleidsByUserid(id);//获取传入用户id的所选角色信息
			for ( Role role : roles ) {//把所有的角色遍历出来
				if ( roleids.contains(role.getId()) ) {//判断遍历出来的角色信息该用户是否拥有
					assingedRoles.add(role);//
				} else {
					unassignRoles.add(role);
				}
			}
			
			model.addAttribute("assingedRoles", assingedRoles);
			model.addAttribute("unassignRoles", unassignRoles);
			
			return "user/assign";
		}
		
		
		@ResponseBody
		@RequestMapping("/pageQuery")
		//queryText表示查询文本
		public Object pageQuery( String queryText, Integer pageno, Integer pagesize ) {
			
			AJAXResult result = new AJAXResult();
			
			try {
				
				// 分页查询
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("start", (pageno-1)*pagesize);
				map.put("size", pagesize);
				map.put("queryText", queryText);
				
				List<User> users = userService.pageQueryData( map );
				// 当前页码			
				// 总的数据条数
				int totalsize = userService.pageQueryCount( map );
				// 最大页码（总页码）
				int totalno = 0;
				if ( totalsize % pagesize == 0 ) {
					totalno = totalsize / pagesize;
				} else {
					totalno = totalsize / pagesize + 1;
				}
				
				// 分页对象传入数据
				Page<User> userPage = new Page<User>();
				userPage.setDatas(users);
				userPage.setTotalno(totalno);
				userPage.setTotalsize(totalsize);//总数据条数
				userPage.setPageno(pageno);
				
				result.setData(userPage);
				result.setSuccess(true);
			} catch ( Exception e ) {
				e.printStackTrace();
				result.setSuccess(false);
			}
			
			return result;
			
		}
		
		//批量删除
		@ResponseBody
		@RequestMapping("/deletes")
		public Object deletes( Integer[] userid ) {
			AJAXResult result = new AJAXResult();
			
			try {
				
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("userids", userid);
				userService.deleteUsers(map);
				result.setSuccess(true);
			} catch ( Exception e ) {
				e.printStackTrace();
				result.setSuccess(false);
			}
			
			return result;
		}
		
		
		
		
		
		//删除单个用户
		@ResponseBody
		@RequestMapping("/delete")
		public Object delete( Integer id ) {
			AJAXResult result = new AJAXResult();
			
			try {
				
				userService.deleteUserById(id);
				result.setSuccess(true);
			} catch ( Exception e ) {
				e.printStackTrace();
				result.setSuccess(false);
			}
			
			return result;
		}
		
		
		@RequestMapping("/index")
		public String index() {
			return "user/index";
			
		}
}
