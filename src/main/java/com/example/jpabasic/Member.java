package com.example.jpabasic;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity // jpa 가 관리한다.
@Table(name = "USER") // 관례
public class Member {

	@Id
	private Long id;

	@Column
	private String name;
}
