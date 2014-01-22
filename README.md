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

=========

How to use!

```
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import br.com.teste.entidades.Car;

import com.jpandroid.core.EntityManager;
import com.jpandroid.core.EntityManagerCore;
import com.jpandroid.core.ORMUtils;
import com.jpandroid.criteria.Query;
import com.jpandroid.database.DatabaseManager;

public class MainActivity extends Activity {
	
	private EntityManager entityManager;
	private DatabaseManager mDatabaseManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		mDatabaseManager = new DatabaseManager(getApplicationContext(), true);
		
		entityManager = EntityManagerCore.getInstance(mDatabaseManager);
		
		for(int a = 0; a < 500; a++) {
			entityManager.insert(new Car("Car " + a, a));
		}
		
		List<Car> select = entityManager.select(Car.class, new Query(Car.class, null, "id"));
		
		List<Car> listByNamedQuery = entityManager.selectListByNamedQuery(Car.class, QueryName.CAR_FIND_ALL, null);
		
		List<Car> select2 = entityManager.select(Car.class, "name like '%1%'", "id");
		
		List<Car> listByNamedQuery2 = entityManager.selectListByNamedQuery(Car.class, QueryName.CAR_FIND_BY_NAME, ORMUtils.getMap(":name", "'%1%'"));
		
		Car selectSingle = entityManager.selectSingle(Car.class, "year = 50", "id");
		
		Car singleByNamedQuery = entityManager.selectSingleByNamedQuery(Car.class, QueryName.CAR_FIND_BY_YEAR, ORMUtils.getMap(":year", "50"));
		
		Integer count = entityManager.count(new Query(Car.class, null, "id"));
		
		Integer max = entityManager.max(Integer.class, new Query(Car.class, null, "id"));
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
```

=========

Create the entities

```
import com.jpandroid.annotations.ForeignKey;
import com.jpandroid.annotations.NamedQuery;
import com.jpandroid.annotations.Primarykey;
import com.jpandroid.annotations.Queries;
import com.jpandroid.annotations.Table;
import com.jpandroid.entity.DomainEntity;
import com.jpandroid.types.GenerationType;
import com.jpandroid.types.LoadType;

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
	
	@ForeignKey(name = "idManufacture", referencedColumnName = "id", load = LoadType.ALL)
	private Manufacture manufacture;
	
	public Car() {
	}
	
	public Car(String name, Integer year) {
		this.name = name;
		this.year = year;
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

	public Manufacture getManufacture() {
		return manufacture;
	}

	public void setManufacture(Manufacture manufacture) {
		this.manufacture = manufacture;
	}
	
}
```

```

import com.jpandroid.annotations.Primarykey;
import com.jpandroid.annotations.Table;
import com.jpandroid.entity.DomainEntity;
import com.jpandroid.types.GenerationType;

@Table
public class Manufacture extends DomainEntity<Integer> {

	@Primarykey(name = "id", strategy = GenerationType.AUTO_INCREMENT)
	private Integer id;
	
	private String name;
	
	public Manufacture() {
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
	
}

```
