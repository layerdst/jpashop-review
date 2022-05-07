## JPQL 
- 테이블이 아닌 객체를 대상으로 검색하는 객체지향 쿼리
- 객체지향 SQL


## JPA
 - 데이터베이스 당 하나씩 부여되는 EntityManagerFactory 는 persistentce.xml 의  설정파일을 토대로 만들어진다.
   ```sh
   EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello")
    ``` 
 - 고객의 요청에 따라 JPA 는 ManagerFactory 에서 EntityManager를 통해 작업이 이루어진다.
   ```sh
   EntityManager em = emf.createEntityManager();
    ``` 
 - JPA 의 모든 데이터 변경은 transaction 안에서 이루어져야 한다. 
   ```sh
   EntitTransaction tx = em.getTransaction();
   tx.begin();
   try{
       ....
        tx.commit();
   }catch(Exception e){
       tx.rollback();
   }finally{
       em.close();
   }
    emf.close();

    ``` 

## JPA 가장 중요한 2가지
- 객체와 관계형 데이터베이스 메핑하기
- 영속성 컨텍스트

## 영속성 컨텍스트
- 엔티티를 영구 저장하는 환경으로 엔티티 매니저를 통하여 접근가능
- EntityManager.persist(entity) - 엔티티를 영속성 컨텍스트에 저장한다
- 엔티티는 바로 DB에 저장되지 않고, transaction 이 commit 하는 시점에 실행된다.
- 어플리케이션과 데이터베이스 사이에 중간계층



## 영속성 컨텍스트 단계 및 생명주기

```sh
객체를 생성한 상태(비영속)
Member member = new Member();
member.setId("1");
member.setUsername("회원1");
 
EntityManager em = emf.createEntityManager();
em.getTransaction().begin();

객체를 저장한 상태 (영속)
em.persist(member);

객체를 영속성 컨텍스트에서 분리(준영속상태)
em.detach(member);

객세츨 삭제한 상태(삭제)
em.remove(member)
```

## 엔티티 조회,  1차캐시
- 영속 엔티티의 동일성을 보장한다. 같은 트랜잭션 안에서 같은 객체를 불러올때 동일성을 보장해준다

    ```sh
    Member member = new Member();
    member.setId("1");
    member.setUsername("회원1");
    
    //1차 캐시에 저장됨
    em.persist(member);
    
    //1차 캐시에서 조회(DB에서 조회하지 않음)
    Member a = em.find(Member.class, "1");
    
    //1차 캐시에서 조회(1차캐시에 저장된 객체는 DB를 조회하지 않음)
    Member b = em.find(Member.class, "1");
    
    //1차 캐시에서 동일한 객체를 불러오면 TRUE 
    System.out.println("result = " + a==b) 
    
    //1차 캐시에서 조회가 되지 않아 DB에서 접속하여 가져옴
    Member findMember2 = em.find(Member.class, "2");

    ```
- 트랜잭션을 지원하는 쓰기 지연 : em.persist 실행시 1차 캐시 뿐만 아니라 '쓰기 지연SQL 저장소 ' 에 쿼리가 저장되고, commit 이 실행되면 이 쿼리들이 DB에 실행된다. 

    ```sh
    Member member1 = new Member();
    member.setId("1");
    member.setUsername("회원1");
    
    Member member2 = new Member();
    member.setId("2");
    member.setUsername("회원2");
    
    //쓰기지연 SQL 저장소에 해당 실행 내용의 쿼리가 순차적으로 저장된다.
    //**batch를 이용하면 DB에 한번에 쿼리를 실행할수 있다.
    em.persist(member1);
    em.persist(member2);
    
    //commit 이 실행되면 쿼리가 실행된다.
    tx.commit();
    ```
- 변경감지 : JPA 가 commit 되는 시점에 Entity 와 스냅샷을 비교하여 수정된 내용 (등록, 삭제, 변경)을 DB에 반영한다. 이를 'flush 발생' 이라고도 한다.
- flush : em.flush(), tx.commit, jpql 쿼리 실행시 실행된다. 
- flush 는 FlushModeType.AUTO -(defualt) , FlushModeType.commit  설정등으로 호출 시점을 달리할 수 있다.
    ```sh
    Member findMember = em.find(Member.class, 150L);
    member.setName("ZZZ");
    
    //DB의 데이터를 변경시에 persist 를 할필요가 없다.
    em.update(member) -> update 코드를 넣지 않아도 update 쿼리가 실행됨
    ```

## 준영속 상태
- 영속 상태의 엔티티가 영속성 콘텍스트에서 분리되는 것으로, commit시 영속성 콘텍스트에 저장되어 있는 쿼리나 엔티티가 적용되지 않는다!
    ```sh
    //특정 엔티티만 준영속 상태로 전환
    em.detach(member);
    
    //EntityManager 안에 있는 엔티티 모두 다 준영속 상태로 전환(초기화)
    em.clear();
    
    //영속성 콘텍스트를 종료
    em.close();
    ```

## 엔티티 매핑
- 객체와 테이블 매핑 
    - @Entity : JPA를 사용해서 테이블과 매핑할 클래스에 @Entity 필수로 넣어야 하며 기본생성자( public, protected) 가 필수이고 final, enum, interface, inner 사용은 금지! 저장할 필드는 final 사용금지
    
        ```sh
        
        @Entity
        //Entity 속성은 name 이 있다
        // name 속성은 내부적으로 적용할 JPA 이름을 쓰는데 다른 패키지의 동일한 클래스가 있을 경우 name 속성을 이용한다. 일반적으로 잘 쓰지 않음 
        
        
        @Table(name="USER")
        //name 테이블 이름 
        //catalog DB catalog 적용
        //calalog DB schema 적용
        //uniqueConstraints 유니크 제약조건
        
        public class Member {
        
            @Id
            private Long id;
            
            @Column(name = "username", unique=true, length = 10)
            private String name;
            ...
        }

        
        ```
- 데이터베이스 스키마 자동생성
    - DDL을 어플리케이션 실행시점에 자동생성
    - 테이블 중심 -> 객체 중심
    - DB 방언을 활용하여 DB 맞는 적절한 DDL 을 생성
    - 생성된 DDL 은 개발 장비에서만 사용
    - 해당 속성은 persistence.xml 에서 자동생성 옵션을 변경할수 있다.
    ```sh
    // hibernate.hbm2ddl.auto 옵션
    <property name="hibernate.hbm2ddl.auto" value="create" />
    
    create : 기존테이블 삭제후 재생성(drop -> create)
    create-drop : create와 같으나 종료후 drop
    update : 변경분만 반영(운영DB 사용 X)
    validate : 엔티티와 테이블이 정상 매핑되었는지만 확인하며 ,맞지 않으면 오류를 발생시킨다.
    none : 사용하지 않음 // 옵션 자체를 삭제해도 같은 옵션이 적용됨
    ```

- DDL 생성 기능
    - 제약조건 추가 : 유일값, 이름은 필수, 10자 초과 X
        ```sh
        @Column(unique=true, nullable=false, length=10)
        private String name;
        ```
    - 유니크 제약조건 추가
        ```sh
        @Table(UniqueConstraints=(
            @UniqueConstraint(name="NAME_AGE_UNIQUE",
            columnNames=("NAME","AGE"))))
        ```
    - DDL 생성 기능은 자동생성될때만 사용되고, JPA 로직에는 영향을 주지 않는다.
    
## 필드와 컬럼매핑
```sh
package hellojpa; 
import javax.persistence.*; 
import java.time.LocalDate; 
import java.time.LocalDateTime; 
import java.util.Date; 

@Entity 

//테이블 이름
@Table(name="MBR")
public class Member { 
    //
     @Id 
     private Long id; 
     
     //컬럼네임을 name 으로 변경,
     //insertable 등록변경 가능여부(default= true)
     //nullable null값 허용여부
     //unique 해당 컬럼에서 적용은 선호하지 않음 @Table 에서 선언함
     @Column(name = "name", insertable=true, updatable = true) 
     private String username; 
     
     //JPA가 integer 숫자타입에 맞는 컬럼이 생성
     private Integer age; 
     
     //enum 타입을 쓰고 싶을때
     //ORDINAL 을 절대 쓰지말고 STRING 타입으로 쓸것
     //ORDINAL 은 ENUM 나열 순서에 따라서 0,1.. 순으로 입력되기 때문에 조심해야한다.
     
     @Enumerated(EnumType.STRING) 
     private RoleType roleType; 
     
     //날짜 타입을 쓰고 싶을때, timestamp, date, time 3가지중 하나를 선택할수 있다.
     @Temporal(TemporalType.TIMESTAMP) 
     private Date createdDate; 
     
     @Temporal(TemporalType.TIMESTAMP) 
     private Date lastModifiedDate; 
     
     //blob, clob 중 큰 데이터를 저장할때
     @Lob 
     private String description; 
     
     //매핑을 하지 않을때 
     @Transient
     private int temp;
     
 //Getter, Setter… 
}

```

## 기본키 맵핑
- ID 직접 할당 : @Id
- 자동생성 : @GenerateValue
    - IDENTITY : 데이터베이스에 위임, MYSQL
    - SEQUENCE : 데이터베이스 시퀀스 오브젝트 사용 - @SequenceGenerator 필요
    - TABLE : 키생성용 테이블 사용, 모든 DB에서 사용 - @TableGenerator 필요
    - AUTO : 방언에 따라 자동지정, 기본값 
    
    ```sh
    @Id
    @GenerateValue(strategy = GenerateType.INDENTITY)
    private String id;
    ```
- 각 생성전략별 특징
    - IDENTITY : JPA 는 영속성컨텍스트가 transaction 이 commit 되는 시점에 저장이 되는데, 이 전략을 쓸경우 EntityManager 가 persist 하는 시점에 insert 쿼리가 실행되고, id 값을 받아, 영속성 컨텍스트의 엔티티에 저장된다.
    
    - SEQUENCE : em.persist 시점에 시퀀스를 가져와서 영속성 컨텍스트의 엔티티에 저장이 되는데, 여러번 호출시에 메모리에 부담이 있을수 있어서 SequenceGenerator 옵션에 allocaotionSize="50" 정도를 설정해 놓는다(DB 풀링과 비슷)


## 연관관계 
- DB는 테이블간의 외래키로 연관관계를 맺는데, 객체는 참조(주소)로 연관관계를 맺는다.
- DB는 외래키를 사용하여 양방향 연관관계를 갖지만 객체는 단방향이다. 
    ```
    객체 -> a.getB().getC
    DB -> A JOIN B , B JOIN A    
    ```
 - 연관관계 표현 
    - @ManyToOne : 다대일 관계
        - fetch type 은 eager 가 default 이니, lazy 로 설정해야함
        - cascade 는 로직에 맞게 수정하는게 맞음
    - @OneToOne : 일대일 관계
        - fetch type 을 lazy로 설정
    - @OneToMany : 일대다 관계
        - fetch type 기본값은 lazy
 
 