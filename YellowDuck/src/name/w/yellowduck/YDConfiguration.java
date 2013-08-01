/* 
 *
 * Copyright (C) 2013 The PlayTractor Team (support@playtractor.com)
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program; if not, see <http://www.gnu.org/licenses/>.
 */


package name.w.yellowduck;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import name.w.yellowduck.activities.misc.YDShape;

import org.cocos2d.types.CGPoint;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class YDConfiguration extends Object {
	public static Context context;
	
	private static YDConfiguration singleton=null;
	
	private int screenUnit, adWidth, adHeight;
	private Category root, activeCategory; 
	private String locale;
	private java.util.Hashtable<String, String> cachedStr;

	public static YDConfiguration sharedConfiguration() {
	    if (singleton == null)
	        singleton=new YDConfiguration();
	    return singleton;
	}
	
	public YDConfiguration() {
		super();
		this.root=new Category();
		this.loadConfigurations();
		
		
		this.cachedStr=new java.util.Hashtable<String, String>();
		
        SharedPreferences sharedPref=PreferenceManager.getDefaultSharedPreferences(context);
		this.locale=sharedPref.getString("_locale_", null);
		if (this.locale == null)
			this.locale=context.getString(R.string.localization);
		this.loadLocalizedString();
	}
	
	public Category getActiveCategory() {
		if (this.activeCategory==null)
			this.activeCategory=root;
		return activeCategory;
	}
	public void setActiveCategory(Category activeCategory) {
		this.activeCategory = activeCategory;
	}

	public boolean isRoot(Category category_) {
		return this.root == category_;
	}

	public java.util.ArrayList<Category> getCategories() {
		return root.getSubCategories();
	}

	public void setScreenUnit(int unit) {
		this.screenUnit=unit;
	}
	
	public int getScreenUnit() {
		return this.screenUnit;
	}

	public int getAdWidth() {
		return adWidth;
	}

	public void setAdWidth(int adWidth) {
		this.adWidth = adWidth;
	}

	public int getAdHeight() {
		return adHeight;
	}

	public void setAdHeight(int adHeight) {
		this.adHeight = adHeight;
	}
	
	public void loadLocalizedString() {
		this.cachedStr.clear();
		try {
			InputStream raw = context.getAssets().open("txt/Localizable_"+locale+".txt");
	        BufferedReader reader = new BufferedReader( new InputStreamReader (raw, "utf16"));
	        String         line = null;
	        while( ( line = reader.readLine() ) != null ) {
	        	int find=line.indexOf("=");
	        	if (find > 0) {
		        	String name=line.substring(0, find).trim();
		        	String value=line.substring(find+1).trim();
		        	name=name.substring(1, name.length()-1);
		        	value=value.substring(1, value.length()-2);
		        	value=value.replaceAll("%@", "%s");
		        	value=value.replaceAll("\\n", "\n");
		        	value=value.replaceAll("\\\\", "");
		        	this.cachedStr.put(name, value);
	        	}
	        }
	        raw.close();
		}
		catch (java.io.IOException e) {
		}
	}
	
	public String getLocale() {
		return this.locale;
	}
	public void setLocale(String l) {
		if (!l.equals(this.locale)) {
			this.locale=l;
			
	        SharedPreferences sharedPref=PreferenceManager.getDefaultSharedPreferences(context);
	        SharedPreferences.Editor editor=sharedPref.edit();
	        editor.putString("_locale_",locale);
	        editor.commit();
	        
	        this.loadLocalizedString();
		}
	}
	public String getLocalizedString(String key) {
		return this.cachedStr.get(key);
	}
	
	private void loadConfigurations() {
		try {
			int tag=0;
			java.util.ArrayList<Category> stack=new java.util.ArrayList<Category>();
			stack.add(root);
			
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	        factory.setValidating(false);
	        XmlPullParser xpp = factory.newPullParser();
	        //Then open the xml file and set as input to  parser:
	        InputStream raw = context.getAssets().open("xml/root.xml");
	        xpp.setInput(raw, null);			
			int eventType=xpp.next();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					String tagName=xpp.getName();
					if (tagName.equals("Category")) {
			            Category category=new Category();
			            category.setName(xpp.getAttributeValue(null, "name"));
			            category.setIcon(this.normalizePath(xpp.getAttributeValue(null, "icon")));
			            category.setAuthor(xpp.getAttributeValue(null, "author"));
			            category.setTag(tag++);
			            
			            Category last=stack.get(stack.size()-1);
			            category.setParent(last);
			            last.addSubCategory(category);
			            //set it as parent category
			            stack.add(category);
					}
					else if (tagName.equals("Activity")) {
			            Category category=new Category();
			            category.setName(xpp.getAttributeValue(null, "name"));
			            category.setType(xpp.getAttributeValue(null, "type"));
			            category.setIcon(this.normalizePath(xpp.getAttributeValue(null, "icon")));
			            category.setAuthor(xpp.getAttributeValue(null, "author"));
			            category.setCredit(xpp.getAttributeValue(null, "credit"));
			            category.setClz(xpp.getAttributeValue(null, "clz"));
			            category.setBg(this.normalizePath(xpp.getAttributeValue(null, "bg")));
			            category.setSettings(xpp.getAttributeValue(null, "settings"));
			            category.setIntr(xpp.getAttributeValue(null, "intr"));
			            category.setDifficulty(this.toInteger(xpp.getAttributeValue(null, "difficulty")));			            
			            category.setTag(tag++);
			            //category.setActivity(true);
			            
			            Category last=stack.get(stack.size()-1);
			            category.setParent(last);
			            last.addSubCategory(category);
					}
				}
				else if (eventType == XmlPullParser.END_TAG) {
					String tagName=xpp.getName();
					if (tagName.equals("Category")) {
						stack.remove(stack.size()-1);
					}
					else if (tagName.equals("Activity")) {
					}
				} 
				eventType=xpp.next();
			}
			raw.close();
		}
		catch (java.io.IOException e) {
		}
		catch (XmlPullParserException e) {
		}
	}
	
	public java.util.ArrayList<YDShape> createShapesFromConfiguration(InputStream raw) {
		java.util.ArrayList<YDShape> stack=new java.util.ArrayList<YDShape>();
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	        factory.setValidating(false);
	        XmlPullParser xpp = factory.newPullParser();
	        //Then open the xml file and set as input to  parser:
	        xpp.setInput(raw, null);			
			int eventType=xpp.next();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					String tagName=xpp.getName();
					if (tagName.equalsIgnoreCase("Shape")) {
						String _resource=xpp.getAttributeValue(null, "pixmapfile");
						String _name=xpp.getAttributeValue(null, "name");
						String _x=xpp.getAttributeValue(null, "x");
						String _y=xpp.getAttributeValue(null, "y");
						String _sound=xpp.getAttributeValue(null, "sound");
						String _type=xpp.getAttributeValue(null, "type");
						String _extra=xpp.getAttributeValue(null, "extra");
						String _voice=null;
			            if (_sound!=null && (_sound.startsWith("voices/$LOCALE/")||_sound.startsWith("sounds/$LOCALE/"))) {
			                _voice=_sound.substring(15);
			                _sound=null;
			            }
			            int shapeType=(_type!=null && _type.equalsIgnoreCase("SHAPE_BACKGROUND"))?YDShape.kShapeBackground:YDShape.kShapeStock;
			            
			            YDShape shape=new YDShape(_resource,shapeType);
			            shape.setName(_name);
			            shape.setSound(this.normalizeAudio(_sound));
			            shape.setVoice(this.normalizeAudio(_voice));
			            shape.setExtra(_extra);
			            shape.setPosition(CGPoint.ccp(this.toFloat(_x), this.toFloat(_y)));
			            stack.add(shape);
					}
					else if (tagName.equalsIgnoreCase("Title")) {
						String _name=xpp.getAttributeValue(null, "name");
						String _x=xpp.getAttributeValue(null, "x");
						String _y=xpp.getAttributeValue(null, "y");
			            int shapeType=YDShape.kShapeLabelText;
			            
			            YDShape shape=new YDShape(null, shapeType);
			            shape.setName(_name);
			            shape.setPosition(CGPoint.ccp(this.toFloat(_x), this.toFloat(_y)));
			            stack.add(shape);
					}
				}
				else if (eventType == XmlPullParser.END_TAG) {
				} 
				eventType=xpp.next();
			}
		}
		catch (java.io.IOException e) {
		}
		catch (XmlPullParserException e) {
		}
		return stack;
	}	
	
	private String normalizeAudio(String audio) {
		if (audio == null)
			return audio;
		return audio.replaceAll(".ogg", ".mp3");
	}
	
	private int toInteger(String str) {
		if (str == null)
			return 0;
		return Integer.parseInt(str);
	}
	private float toFloat(String str) {
		if (str == null)
			return 0;
		return Float.parseFloat(str);
	}
	private String normalizePath(String path) {
	//	@harsh : change "Assets" with "assets"
		if (path != null&& path.startsWith("assets/")) {
			path=path.substring(7);
		}
		return path;
	}
}
