INSERT INTO `role`(id,name) VALUES (1,'ROLE_EMPLOYEE'),(2,'ROLE_ADMIN');
INSERT INTO `employee`(id, username, password, first_name, last_name, email, top_employee, version) VALUES (2,'admin','{noop}admin','Admin','Admin','admin@alten.it',0,10);
INSERT INTO `employees_roles`(employee_id, role_id) VALUES (2,2);