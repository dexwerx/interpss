package org.interpss.editor.coreframework;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.PlainDocument;

import org.bounce.text.ScrollableEditorPanel;
import org.bounce.text.xml.XMLDocument;
import org.bounce.text.xml.XMLEditorKit;
import org.bounce.text.xml.XMLStyleConstants;
import org.interpss.editor.doc.IpssProject;
import org.interpss.editor.resources.Translator;


public class IpssXmlDocument extends IpssEditorDocument{
	
	/**
	 * Container for the graph so that you can scroll over the graph
	 */
	protected JEditorPane editor;

	protected IpssXmlFile docFile;
	
	public IpssXmlDocument(GPGraphpad gp, IpssProject p, String name,IpssXmlFile file) {
		super();
		
		setDoubleBuffered(true);
		updateUI();
		
		this.setName(name);
		setGraphpad(gp);
		setProject(p);
		
		if (file != null) {
			this.docFile = file;
		} else {
			this.docFile = new IpssXmlFile(name);
		}
		
		setBorder(BorderFactory.createEtchedBorder());
		setLayout(new BorderLayout());
		

		
		
		editor = new JEditorPane();

		// Instantiate a XMLEditorKit with wrapping enabled.
		XMLEditorKit kit = new XMLEditorKit(true);

		// Set the wrapping style.
		kit.setWrapStyleWord(true);

		editor.setEditorKit(kit);
		String fontName = Translator.getString("XMLDocument.FontName");
		int fontSize = Integer.parseInt(Translator.getString("XMLDocument.FontSize"));
		int fontStyle = Integer.parseInt(Translator.getString("XMLDocument.FontStyle"));
		
		editor.setFont(new Font(fontName, fontStyle, fontSize));
//		mainTextArea.setEditable(false);
		
		// Set the tab size
		editor.getDocument().putProperty(PlainDocument.tabSizeAttribute,
				new Integer(4));

		// Enable auto indentation.
		editor.getDocument().putProperty(
				XMLDocument.AUTO_INDENTATION_ATTRIBUTE, new Boolean(true));

		// Enable tag completion.
		editor.getDocument().putProperty(
				XMLDocument.TAG_COMPLETION_ATTRIBUTE, new Boolean(true));

		// Set a style
		kit.setStyle(XMLStyleConstants.ATTRIBUTE_NAME,
				new Color(255, 0, 0), Font.BOLD);

		// Put the editor in a panel that will force it to resize, when a
		// different
		// view is choosen.
		ScrollableEditorPanel editorPanel = new ScrollableEditorPanel(
				editor);

		JScrollPane scrollPane = new JScrollPane(editorPanel);

		this.add(BorderLayout.CENTER, scrollPane);

//		mainTextArea = new JTextArea(UIConfig.MainTextArea_Row, UIConfig.MainTextArea_Col);
//		mainTextArea = new JTextArea();
//		mainTextArea.setEditable(false);
//		
//		scrollPane = new JScrollPane(mainTextArea);
//		
//		this.add(BorderLayout.CENTER, scrollPane);
		
//		mainTextArea.setText(file.getFilePathName());

	      if (file == null)
		        return;

	      File textfile = new File(file.getFilePathName());

	      FileReader reader = null;
	      try {
	        reader = new FileReader(textfile);
	        editor.read(reader, null);
	      } catch (IOException ex) {
	        JOptionPane.showMessageDialog(this.getGraphpad().getFrame(),
	            "File Not Found", "ERROR", JOptionPane.ERROR_MESSAGE);
	      } finally {
	        if (reader != null) {
	          try {
	            reader.close();
	          } catch (IOException x) {
	          }
	        }
	      }
	      editor.getDocument().addDocumentListener(new DocumentListener(){
				
			    public void insertUpdate(DocumentEvent e)
			    {
			    	setModified(true);
			    }
			    public void removeUpdate(DocumentEvent e)
			    {
			    	setModified(true);
			    }
			    public void changedUpdate(DocumentEvent e)
			    {
			    	setModified(true);
			    }
				
			});
	}
	
	public IpssXmlFile getDocFile() {
		return this.docFile;
	}
	
	public boolean isModified() {
		return this.docFile.isModified();
	}
	
	public void setModified(boolean dirty) {
		this.modified = dirty;
		graphpad.refreshDocumentEditorPanel(this);
	}
	
	/**
	 * return the main text area for text project
	 * 
	 * @return
	 */
	public String getText() {
		return editor.getText();
	}
	
	public boolean close(boolean showConfirmDialog){
		// set default to save on close
		int r = JOptionPane.YES_OPTION;

		if (isModified()) {
			if (showConfirmDialog)
				r = JOptionPane.showConfirmDialog(getGraphpad().getFrame(), "'"
						+ getFileName() + "'"
						+ Translator.getString("XMLSaveChangesDialog"),
						Translator.getString("Title"),
						JOptionPane.YES_NO_CANCEL_OPTION);

			// if yes, then save and close
			if (r == JOptionPane.YES_OPTION) {
				getGraphpad().getCommand("FileSave").actionPerformed(null);
				return true;
			}
			// if no, then don't save and just close
			else if (r == JOptionPane.NO_OPTION) {
				return true;
			}
			// all other conditions (cancel and dialog's 'X' button)
			// don't save and don't close
			else
				return false;
		}
		return true;
	}
}
