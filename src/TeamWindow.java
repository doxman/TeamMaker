import java.awt.*;
import java.awt.print.*;

import javax.swing.JDialog;


@SuppressWarnings("serial")
public class TeamWindow extends JDialog implements Printable {

	public TeamWindow(Frame owner, boolean modal) throws HeadlessException {
		super(owner, modal);
		// TODO Auto-generated constructor stub
	}

	public TeamWindow(Frame owner, String title) throws HeadlessException {
		super(owner, title);
		// TODO Auto-generated constructor stub
	}

	public TeamWindow(Frame owner, String title, boolean modal)
			throws HeadlessException {
		super(owner, title, modal);
		// TODO Auto-generated constructor stub
	}

	public TeamWindow(Frame owner, String title, boolean modal,
			GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
		// TODO Auto-generated constructor stub
	}

	public TeamWindow(Dialog owner, String title, boolean modal,
			GraphicsConfiguration gc) throws HeadlessException {
		super(owner, title, modal, gc);
		// TODO Auto-generated constructor stub
	}

	public int print(Graphics g, PageFormat pf, int page)
			throws PrinterException {
		
		if (page > 0)
		{
			return NO_SUCH_PAGE;
		}
		
		Graphics g2d = (Graphics2D)g;
		g2d.translate((int)pf.getImageableX(), (int)pf.getImageableY());
		
		printAll(g);
		
		return PAGE_EXISTS;
	}

}
