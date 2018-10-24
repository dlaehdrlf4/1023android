package com.gil.android;

import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.gil.android.dao.ItemDao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:src/main/webapp/WEB-INF/spring/**/*.xml" })
public class ItemTest {
	@Autowired
	private SqlSession sqlSession;

	@Autowired
	private ItemDao itemdao;
	@Test
	public void sqlSessionTest(){
		//System.out.println(sqlSession);
		//System.out.println(sqlSession.selectList("item.listItem"));
		//에러가 발생해서 문자열로 만들어 주었다.
		//System.out.println(sqlSession.selectOne("item.getItem",0)+"");
		
		//System.out.println(itemdao);
		//System.out.println(itemdao.listItem());
		System.out.println(itemdao.getItem(2));
	}
}