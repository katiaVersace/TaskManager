package com.alten.springboot.taskmanager.dataservice;


import org.springframework.stereotype.Service;

@Service
public class SecurityDataService {
  
   public boolean isOwner(int id1, int id2) {
	   System.out.println(id1);
	   System.out.println(id2);
	    return id1 == id2;
   }
}
