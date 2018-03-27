public class Project5
{	
	public static void main(String[] args)
	{
		System.out.println("Starting Project 5 - SERVER...");
		new Thread(new Runnable()
			{
				public void run()
				{
					new MainFrame();
				}
			}).start();
		System.out.println("Project 5 - SERVER Started!");
	}//End Main()
}//end Project5