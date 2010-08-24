package earley2;
import java.util.*;

public class CustomerTable
{
	public HashMap customers = new HashMap();

	public void addCustomer(int C, String lookingFor, Entry customer)
	{
		String key = C + " " + lookingFor;

		if (customers.containsKey(key))
		{
			LinkedList customerGroup = (LinkedList) customers.get(key);
			customerGroup.addLast(customer);
		}
		else
		{
			LinkedList customerGroup = new LinkedList();
			customerGroup.addLast(customer);
			customers.put(key, customerGroup);
		}
	}

	public ListIterator getCustomers(int C, String lookingFor)
	{
		String key = C + " " + lookingFor;

		LinkedList customerGroup = (LinkedList) customers.get(key);
		ListIterator li = customerGroup.listIterator();
		return li;
	}
}