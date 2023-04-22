package com.leni.batchjob;

import org.springframework.batch.item.ItemProcessor;

public class EmpItemProcessor implements ItemProcessor<Employee, Employee>{

	@Override
	public Employee process(final Employee employee) throws Exception {
		Employee processedEmployee = new Employee();
		
		processedEmployee.setId(employee.getId());
		processedEmployee.setName(employee.getName().toUpperCase());
		processedEmployee.setDept(employee.getDept().toUpperCase());
		processedEmployee.setSalary(employee.getSalary());
		
		return processedEmployee;
	}

}
