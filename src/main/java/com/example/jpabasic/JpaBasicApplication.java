package com.example.jpabasic;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import java.util.List;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JpaBasicApplication {

	// 추후 스프링으로 넘어가면 모든 코드는 스프링이 알아서 처리하고, em.persist 와 같은 코드 한 줄만 실행하면 된다.
	public static void main(String[] args) {

		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello"); // 팩토리는 커넥션 풀 같은 느낌, 한 번만 생성한다.
		EntityManager em = emf.createEntityManager(); // 실행 단위 별로 매니저를 생성한다. 이 엔티티 매니저는 커넥션 풀처럼 스레드끼리 공유되어선 안 된다. 바로 반환해주자.

		// 트랜잭션 시작 (JPA 에서 발생하는 모든 변경 사항들은 ★★★ 모두 "트랜잭션 내" 에서 작업되어야 한다. (데이터 커넥션 받아서 작업되어야 한다.))
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		try {
			Member member = new Member();
			member.setId(1L);
			member.setName("hello");

			// 1. DB 에 저장한다.
			em.persist(member);

			// 2. DB 에서 ID 가 1인 엔티티를 조회한다.
			Member findMember = em.find(Member.class, 1L); // entityManager 는 자바 컬렉션 같은 느낌이다. 엔티티를 관리하고 있다고 보면 된다.
			System.out.println("findMember.id = " + findMember.getId());

			// 3. DB 에서 엔티티를 수정한다. -> 이때 em.persist 를 호출하지 않아도 된다. ★★★ entityManager (JPA) 가 트랜잭션을 커밋하는 시점에 엔티티 변경 사항을 체크한다.
			findMember.setName("updated hello");
			// em.persist(member);

			// 4. JPQL -> JPA 입장에서는 코드를 DB 대상이 아니라 엔티티 대상을 기준으로 작업한다.
			List<Member> result = em.createQuery("select m from Member as m", Member.class)
				.setFirstResult(1)
				.setMaxResults(10) // 객체지향 쿼리라서 페이지네이션이 쉬움 ★★★ JPQL 의 엄청난 장점
				.getResultList();

			for (Member m : result) {
				System.out.println("member.name = " + m.getName());
			}

			tx.commit();

		} catch (Exception e) {
			tx.rollback();

		} finally {
			em.close();
		}

		emf.close(); // entityManager 다 닫으면 팩토리도 닫아야 한다.

		SpringApplication.run(JpaBasicApplication.class, args);
	}

}
