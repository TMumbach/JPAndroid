JPAndroid
=========

Android ORM framework

Features:

* Automatic creation of tables.
* Object-relational mapping.
* Load database from sdcard.
* Export database file.
* Mapping InputText for automatic processing.
* And more..


How to use

Entity:

<code>
import com.jpandroid.annotations.NamedQuery;
import com.jpandroid.annotations.Primarykey;
import com.jpandroid.annotations.Queries;
import com.jpandroid.annotations.Table;
import com.jpandroid.entity.DomainEntity;
import com.jpandroid.types.GenerationType;

@Table("Car")
@Queries({ 
	@NamedQuery(name = "findAllCar", columns = {"id"}),
	@NamedQuery(name = "findCarByName", selection = "name like :name", columns = {"id"}),
	@NamedQuery(name = "findCarByYear", selection = "year = :year", columns = {"id"}) 
})

public class Car extends DomainEntity<Integer> {
	
	@Primarykey(name = "id", strategy = GenerationType.AUTO_INCREMENT)
	private Integer id;
	
	private String name;
	
	private Integer year;
	
	private String manufacture;
	
	public Car() {
	}
	
  public Car(String name, Integer year, String manufacture) {
		this.name = name;
		this.year = year;
		this.manufacture = manufacture;
	}
	
	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public String getManufacture() {
		return manufacture;
	}

	public void setManufacture(String manufacture) {
		this.manufacture = manufacture;
	}

}
</code>

Using:

<code>

import com.jpandroid.core.EntityManager;
import com.jpandroid.core.EntityManagerCore;
import com.jpandroid.database.DatabaseManager;
import com.jpandroid.entity.DomainEntity;
import com.jpandroid.entity.Entity;

import dalvik.system.DexFile;

public class MainActivity extends Activity {
	
	EntityManager entityManager;
	DatabaseManager mDatabaseManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mDatabaseManager = new DatabaseManager(getApplicationContext(), true);
		entityManager = EntityManagerCore.getInstance(mDatabaseManager);
		
		
		for(int a = 0; a < 500; a++) {
			entityManager.insert(new Car("Car " + a, a, "Manu " + a));
		}
		
		entityManager.select(Car.class, new Query(Car.class, null, "id"));
		
		entityManager.getListByNamedQuery(Car.class, QueryName.CAR_FIND_ALL, null);
		
		entityManager.select(Car.class, "name like '%1%'", "id");
		
		entityManager.getListByNamedQuery(Car.class, QueryName.CAR_FIND_BY_NAME, ORMUtils.getMap(":name", "'%1%'"));
		
		entityManager.selectSingle(Car.class, "year = 50", "id");
		
		entityManager.getSingleByNamedQuery(Car.class, QueryName.CAR_FIND_BY_YEAR, ORMUtils.getMap(":year", "50"));
		
		
	}

}

</code>


Object-relational mapping:
<code>
@Table("User")
public class User extends DomainEntity<Integer>
{
        @Primarykey(name = "id", strategy = GenerationType.AUTO_INCREMENT)
        private Integer id;

        private String login;

        @ForeignKey(name = "idCity", referencedColumnName = "id")
        private City city;

        public User()
        { }

        @Override
        public Integer getId() 
        {
                return id;
        }

        @Override
        public void setId(Integer id) 
        {
                this.id = id;
        }
        
        public void setLogin(String login)
        {
                this.login = login;
        }

        public String getLogin()
        {
                return login;
        }

        public City getCity() {
                return city;
        }

        public void setCity(City city) {
                this.city= city;
        }
}

</code>

<code>

@Table("City")
public class City extends DomainEntity<Integer>
{
        @Primarykey(name = "id", strategy = GenerationType.AUTO_INCREMENT)
        private Integer id;

        private String name;

        public City()
        { }

        @Override
        public Integer getId() 
        {
                return id;
        }

        @Override
        public void setId(Integer id)  
        {
                this.id = id;
        }


        public String getName() 
        {
                return name;
        }


        public void setName(Name name)  
        {
                this.name = name;
        }
}

</code>
