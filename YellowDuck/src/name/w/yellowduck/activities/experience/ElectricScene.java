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


package name.w.yellowduck.activities.experience;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;

import name.w.yellowduck.Category;
import name.w.yellowduck.YDConfiguration;
import name.w.yellowduck.YDLayerBase;

import org.cocos2d.actions.instant.CCCallFuncN;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCScaleTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.menus.CCMenuItemSprite;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCTextureCache;
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.ccColor3B;

import android.util.FloatMath;
import android.view.MotionEvent;

public class ElectricScene extends name.w.yellowduck.YDActLayerBase {
	static {
    	System.loadLibrary("w_yd_gnucap");
	}
	private static native int callGnuCap(String fileName);
	
	private final String kGnuCapDelimiter            ="~";
	private final int kDistanceAsSelected=15;
	
	private class ElectricNode extends CCSprite {
		private float x, y;
		private java.util.ArrayList<Wire> wires; //Wire
		private String name;
		private ElectricComponent component; //
		
		
		public String getName() {
			return name;
		}
		public ElectricComponent getComponent() {
			return component;
		}
		public void setComponent(ElectricComponent component) {
			this.component = component;
		}
		
		private ElectricNode(String name_, float x_, float y_){
			super("dummy.png");
	        x=x_;
	        y=y_;
	        name=name_;
	        wires=new java.util.ArrayList<Wire>();
		}
		public void addWire(Wire wire_) {
		    wires.add(wire_);
		}

		// Remove a wire from this node and reasign all wires
		// already connected to this node a new given wire number
		public void removeWire(Wire wire_){
		    wires.remove(wire_);
		}
		public java.util.ArrayList<Wire> getWires() {
		    return wires;
		}
		// Has wire, return True if the given wire is already connected
		// to this node
		/*
		public boolean hasWire(Wire wire_) {
		    return wires.contains(wire_);
		}
		*/
		public boolean isWired() {
		    return !wires.isEmpty();
		}

		public void show(YDLayerBase parent) {
			CCTexture2D texture=parent.textureFromExpansionFile("image/activities/experience/electric/connect.png");
			super.setTexture (texture);
			super.setTextureRect(CGRect.make(0,0,texture.getWidth(), texture.getHeight()));
		    super.setScale(component.getScale());
		    CGPoint pt=component.getPosition();
		    
		    float w=component.getContentSize().width*component.getScale();
		    float h=component.getContentSize().height*component.getScale();
		    //left-bottom
		    float x0=pt.x-w/2;
		    float y0=pt.y-h/2;
		    x0 += x*w;
		    y0 += y*h;
		    
		    super.setPosition(x0, y0);
		    parent.addChild(this, 2);
		}
		public void move(float xOffset, float yOffset) {
		    for (Wire _wire : wires) { //Wire is ElectricComponent
		    	_wire.move(xOffset, yOffset);
		    }
		    CGPoint pt=super.getPosition();
		    pt.x += xOffset;
		    pt.y += yOffset;
		    super.setPosition(pt);
		}

		public void remove() {
		    for (Wire wire : wires) {
		        if (wire.getSrc() == this)
		            wire.setSrc(null);
		        else if (wire.getDst()==this) {
		            wire.setDst(null);
		        }
		        wire.remove();
		    }
		    super.removeFromParentAndCleanup(true);
		}
		public boolean hit(CGPoint pt) {
		    float w=super.getContentSize().width*super.getScale();
		    float h=super.getContentSize().height*super.getScale();
		    CGRect rc=CGRect.make(super.getPosition().x-w/2, super.getPosition().y-h/2, w, h);
		    return rc.contains(pt.x, pt.y);
		}
	}
	
	private class ElectricComponent extends CCSprite {
	    private String gnucapName;
	    private String gnucapValue;
	    private java.util.ArrayList<ElectricNode> nodes;
	    
	    float voltage, intensity;
	    
	    private CCLabel annotation;
	    private float xAnnotationOffset, yAnnotationOffset;
	    
	    private int counter;
	    
	    private String img;
	    
		public String getGnucapName() {
			return gnucapName;
		}
		public String getGnucapValue() {
			return gnucapValue;
		}
		public void setGnucapValue(String gnucapValue) {
			this.gnucapValue = gnucapValue;
		}
		public float getVoltage() {
			return voltage;
		}
		public float getIntensity() {
			return intensity;
		}
		public CCLabel getAnnotation() {
			return annotation;
		}
		public void setCounter(int counter) {
			this.counter = counter;
		}

		private ElectricComponent(String img_, String name_, String value_) {
			super("dummy.png");
			img=img_;

	        gnucapName=name_;
	        gnucapValue=value_;
	        nodes = new java.util.ArrayList<ElectricNode>();
	        
	        String fontName="normal";
	        int fontSize=8;
	        annotation=CCLabel.makeLabel(" ", fontName, fontSize);
	        xAnnotationOffset=yAnnotationOffset=0;
		}

		public void addNode(ElectricNode node_) {
		    nodes.add(node_);
		    node_.setComponent(this);
		}

		public void move(float xOffset, float yOffset) {
		    for (ElectricNode node : nodes) {
		        node.move(xOffset, yOffset);
		    }
		    CGPoint pt=super.getPosition();
		    pt.x += xOffset;
		    pt.y += yOffset;
		    super.setPosition(pt);
		    pt.x += xAnnotationOffset;
		    pt.y += yAnnotationOffset;
		    annotation.setPosition(pt);
		}

		public void setVoltageIntensity(boolean validValue_, float voltage_, float intensity_) {
		    voltage = voltage_;
		    intensity = intensity_;
		    if (validValue_) {
		        String str=String.format("V=%.2fV\nI=%.3fA", voltage, intensity);
		        annotation.setString(str);
		    }
		    else {
		        annotation.setString(" ");
		    }
		}
		public void show(YDLayerBase parent) {
			CCTexture2D texture=parent.textureFromExpansionFile(this.normalizedResource(img));
			super.setTexture (texture);
			super.setTextureRect(CGRect.make(0,0,texture.getWidth(), texture.getHeight()));
		    parent.addChild(this, 2);
		    annotation.setPosition(super.getPosition().x+xAnnotationOffset, super.getPosition().y+yAnnotationOffset);
		    parent.addChild(annotation, 5);
		    for (ElectricNode node : nodes) {
		        node.show(parent);
		    }
		}
		public void remove() {
		    for (ElectricNode node : nodes) {
		        node.remove();
		    }
		    super.removeFromParentAndCleanup(true);
		    annotation.removeFromParentAndCleanup(true);
		}

		public java.util.ArrayList<ElectricNode> getNodes() {
		    return nodes;
		}
		/*
		# Return True if this component is connected and can provides a gnucap
		# description
		#
		# It assume that if a single node is not connected, the whole
		# component is not connected
		*/
		public boolean isConnected() {
		    boolean wired=false;
		    for (ElectricNode node : nodes) {
		        if (!node.isWired())
		            return false;
		        else
		            wired=true;
		    }
		    return wired;
		}
		public String getFullGnucapName() {
		    return gnucapName + counter;
		}

		// Return the gnucap definition for this component
		// model is optional
		public String toGnucap(String model) {
		    // ignore component if there are some unconnected node.
		    if (!this.isConnected())
		        return "";
		    
		    String fullGnucapName=this.getFullGnucapName();
		    String gnucap = getFullGnucapName();
		    /*
		    # No definition, it happens for connection spot
		    # But in this case, it should not be called at all. it's not in the top level
		    # components list.
		    */
		    for (ElectricNode node : nodes) {
		        Wire firstWire=node.getWires().get(0);
		        gnucap += " " + firstWire.getWireId();
		    }
		    gnucap += " "+gnucapValue+"\n";
		    if (model!=null)
		        gnucap += model;
		    
		    //[gnucap, ".print dc + v(%s) i(%s)\n" %(self.gnucap_name, self.gnucap_name)]
		    return gnucap+String.format("%s.print dc + v(%s) i(%s)\n", kGnuCapDelimiter, fullGnucapName, fullGnucapName);
		}

		public void replaceTextureWith(String name) {
			CCNode _parent=super.getParent();
			if (_parent instanceof YDLayerBase) {
				YDLayerBase  parent=(YDLayerBase)_parent;
			    CCTexture2D tex = parent.textureFromExpansionFile(this.normalizedResource(name));
			    super.setTexture(tex);
			}
		}

		public boolean hit(CGPoint pt) {
		    float w=super.getContentSize().width*super.getScale();
		    float h=super.getContentSize().height*super.getScale();
		    CGRect rc=CGRect.make(super.getPosition().x-w/2, super.getPosition().y-h/2, w, h);
		    return rc.contains(pt.x, pt.y);
		}
		//move annotation to property position
		public void setAnnotationOffset(int xOffset_, int yOffset_) {
		    xAnnotationOffset=xOffset_;
		    yAnnotationOffset=yOffset_;
		}

		public void showAnnotation(boolean show) {
		    annotation.setVisible(show);
		}

		public boolean onClick(CGPoint pt) {
		    return false;
		}

		private String normalizedResource(String name) {
		    if (!name.startsWith("image/"))
		        name="image/activities/experience/electric/" + name;
		    return name;
		}
		//static
		public void normalize(java.util.ArrayList<ElectricComponent>components) {
		    //Assign wire_id to each wire
		    for (ElectricComponent component : components) {
		        if (component instanceof Wire) {
		            Wire wire=(Wire)component;
		            wire.setWireId(-1);
		        }
		    }
		    boolean more=true;
		    int activeId=1; //start from 1
		    while (more) {
		        more=false;
		        for (ElectricComponent component : components) {
		            if (component instanceof Wire) {
		                Wire wire=(Wire)component;
		                if (wire.getWireId() < 0 && wire.getSrc()!=null && wire.getDst()!=null) {
		                    more=true;
		                    wire.normalizeNode(wire.getSrc(), activeId);
		                }
		            }
		            if (more)
		                break;
		        }
		        ++activeId;
		    }
		    //assign a unique counter to each component
		    java.util.Hashtable<String, Integer> dict=new java.util.Hashtable<String, Integer>();  
		    for (ElectricComponent component : components) {
		        if (component instanceof Wire)
		            continue;
		        String key=component.getGnucapName();
		        Integer number=dict.get(key);
		        int n=((number==null)?0:number.intValue()) + 1;
		        component.setCounter(n);
		        dict.put(key, Integer.valueOf(n));
		    }
		}

		public float angleFromPoint(CGPoint p1, CGPoint p2) {
		    float angle = (float)(Math.atan2(p1.y - p2.y, p1.x - p2.x)) * 180 /3.14f;
		    angle = -1*angle;
		    
		    return angle;
		}
	}

	private class Wire extends ElectricComponent {
		private int wireId;
		private boolean removed;
		private ElectricNode src, dst;
		public int getWireId() {
			return wireId;
		}
		public void setWireId(int wireId) {
			this.wireId = wireId;
		}
		public boolean isRemoved() {
			return removed;
		}
		public ElectricNode getSrc() {
			return src;
		}
		public void setSrc(ElectricNode src) {
			this.src = src;
		}
		public ElectricNode getDst() {
			return dst;
		}
		public void setDst(ElectricNode dst) {
			this.dst = dst;
		}
		
		private Wire() {
			super("wire.png", "___", "");
		    super.showAnnotation(false);
		}
		
		//Wire is used to connect two components so it is possible that it is called to remove multiple times
		public void remove() {
		    if (!removed) {
		        if (dst!=null) {
		            dst.removeWire(this);
		            dst=null;
		        }
		        if (src!=null) {
		            src.removeWire(this);
		            src=null;
		        }
		        super.remove();
		        removed=true;
		    }
		}

		public void show(YDLayerBase parent) {
		    removed=false;
		    
		    this.move(0,  0);
		    //set a random color
		    int random=1;
		    int r=(random>0)?0xff:0;
		    int g=(random>0)?0xff:0;
		    int b=(random>0)?0xff:0;
		    super.setColor(ccColor3B.ccc3(r,g,b));
		    super.show(parent);
		}

		//Make sure both the src and target nodes are in correct position
		public void move (float xOffset, float yOffset) {
		    float xDiff=dst.getPosition().x-src.getPosition().x;
		    float yDiff=dst.getPosition().y-src.getPosition().y;
		    float distance=FloatMath.sqrt(xDiff*xDiff+yDiff*yDiff);
		    super.setScaleX(distance/super.getContentSize().width);
		    super.setScaleY(2.0f/super.getContentSize().height);
		    
		    super.setRotation(angleFromPoint(dst.getPosition(), src.getPosition()));
		    super.setPosition((dst.getPosition().x+src.getPosition().x)/2, (dst.getPosition().y+src.getPosition().y)/2);
		}

		public boolean hit(CGPoint pt) {
		    if (tooFarFromPoint(pt, src.getPosition(), dst.getPosition()))
		        return false;
		    return distanceFromPoint(pt, src.getPosition(), dst.getPosition()) <= kDistanceAsSelected;
		}

		//distance of a point to a line defined by p1 and p2
		private float distanceFromPoint(CGPoint p0, CGPoint p1, CGPoint p2) {
		    float distance=(p2.x-p1.x)*(p1.y-p0.y)-(p1.x-p0.x)*(p2.y-p1.y);
		    if (distance < 0)
		        distance=0-distance;
		    float diff1=p2.x-p1.x;
		    float diff2=p2.y-p1.y;
		    float diff=FloatMath.sqrt(diff1*diff1 + diff2*diff2);
		    distance=distance/diff;
		    
		    return distance;
		}

		//static
		public void normalizeNode(ElectricNode node , int activeId) {
			java.util.ArrayList<ElectricNode> collect=new java.util.ArrayList<ElectricNode>(); 
		    for (Wire wire : node.getWires()) {
		        if (wire.getWireId() < 0) {
		            wire.setWireId(activeId);
		            if (wire.getSrc() == node) {
		                if (!collect.contains(wire.getDst()))
		                    collect.add(wire.getDst());
		            }
		            else {
		                if (!collect.contains(wire.getSrc()))
		                    collect.add(wire.getSrc());
		            }
		        }
		    }
		    Wire wire=new Wire();
		    for (ElectricNode more : collect) {
		    	wire.normalizeNode(more, activeId);
		    }
		}

		private boolean tooFarFromPoint(CGPoint pt, CGPoint p1, CGPoint p2)  {
		    float xMin=p1.x, xMax=p1.x;
		    float yMin=p1.y, yMax=p1.y;
		    if (p2.x < xMin)
		        xMin=p2.x;
		    else if (p2.x > xMax)
		        xMax=p2.x;
		    if (p2.y < yMin)
		        yMin=p2.y;
		    else if (p2.y > yMax)
		        yMax=p2.y;
		    xMin -= kDistanceAsSelected;
		    xMax += kDistanceAsSelected;
		    yMin -= kDistanceAsSelected;
		    yMax += kDistanceAsSelected;
		    
		    return  (pt.x < xMin || pt.x > xMax || pt.y < yMin || pt.y > yMax);
		}
		
	}
	private class Battery extends ElectricComponent {
		private Battery(String value_) {
		    //50x128
		    super("battery.png", "V", value_); //Vsupply

	        ElectricNode nodeA=new ElectricNode("A", 0.5f, 1.0f);
	        super.addNode(nodeA);
	        
	        ElectricNode nodeB=new ElectricNode("B", 0.5f, 0);
	        super.addNode(nodeB);
		}

		// Return False if we need more value to complete our component
		// This is usefull in case where one Component is made of several gnucap component
		public void setVoltageIntensity(boolean validValue_, float voltage_, float intensity_) {
		    super.setVoltageIntensity(validValue_,voltage_ ,intensity_);
		    float intensityCheck=intensity_;
		    if (intensityCheck < 0)
		        intensityCheck=0-intensityCheck;
		    if (intensityCheck > 1) {
		        //Short circuit case, set the dead battery icon
		        super.replaceTextureWith("battery_dead.png");
		    }
		    else {
		        super.replaceTextureWith("battery.png");
		    }
		}
	}
	private class Switch extends ElectricComponent {
		private String valueOn, valueOff;
		
		private Switch() {
		    //108*42
		    super("switch_off.png","R","10000k");//switch is off

	        super.showAnnotation(false);
	        
	        ElectricNode nodeA=new ElectricNode("A",0,10.0f/42);
	        super.addNode(nodeA);
	        
	        ElectricNode nodeB=new ElectricNode("B", 1.0f, 10.0f/42);
	        super.addNode(nodeB);
	        
	        valueOn ="0";
	        valueOff="10000K";
		}
		//Override
		public boolean onClick(CGPoint pt) {
		    this.toggleStatus();
		    return true;
		}

		private void toggleStatus() {
		    boolean isOn=super.getGnucapValue().equals(valueOn);
		    if (isOn) {
		        super.setGnucapValue(valueOff);
		        super.replaceTextureWith("switch_off.png");
		    }
		    else {
		        super.setGnucapValue(valueOn);
		        super.replaceTextureWith("switch_on.png");
		    }
		}
	}
	private class Switch2 extends ElectricComponent {
		private String valueOn, valueOff;
		private Switch2() {
			super("switch2_off.png", "R", "10000k");//switch is off
	        super.showAnnotation(false);
	        
	        ElectricNode nodeC=new ElectricNode("C", 0, 25.0f/72);
	        super.addNode(nodeC);

	        ElectricNode nodeA=new ElectricNode("A", 1, 48.0f/72);
	        super.addNode(nodeA);

	        ElectricNode nodeB=new ElectricNode("B", 1, 6.0f/72);
	        super.addNode(nodeB);
	        
	        valueOn ="0";
	        valueOff="10000K";
		}
		//Override
		public boolean isConnected() {
		    //The switch needs at least 2 connected nodes
		    int count=0;
		    for (ElectricNode node : getNodes()) {
		        if (node.isWired()) {
		            ++count;
		        }
		    }
		    return count >= 2;
		}

		//Override
		public boolean onClick(CGPoint pt) {
		    this.toggleStatus();
		    return true;
		}

		private void toggleStatus() {
		    boolean isOn=getGnucapValue().equals(valueOn);
		    if (isOn) {
		        super.setGnucapValue(valueOff);
		        super.replaceTextureWith("switch2_off.png");
		    }
		    else {
		        super.setGnucapValue(valueOn);
		        super.replaceTextureWith("switch2_on.png");
		    }
		}


		// Return the gnucap definition for a single resitor of the switch2
		// node_id1 and node_id2 are the index in the list of nodes
		private String toGnucapRes(String gnucapName, ElectricNode node1, ElectricNode node2, String gnucapValue) {
		    String gnucap = gnucapName + " ";
		    ElectricNode _nodes[]={node1, node2};
		    for (int i = 0; i < 2; ++i){
		        ElectricNode node=_nodes[i];
		        Wire first=node.getWires().get(0);
		        gnucap += ""+first.getWireId()+" ";
		    }
		    gnucap+=gnucapValue+"\n";
		    
		    return gnucap+String.format("%s.print dc + v(%s) i(%s)\n", kGnuCapDelimiter, gnucapName, gnucapName);
		}

		//Override
		public String toGnucap(String model) {
		    String fullGnucapName=getFullGnucapName();
		    
		    ElectricNode node0=getNodes().get(0);
		    ElectricNode node1=getNodes().get(1);
		    ElectricNode node2=getNodes().get(2);
		    
		    boolean isOn=getGnucapValue().equals(valueOn);
		    String valueTop, valueBottom;
		    if (isOn) {
		        valueTop = valueOn;
		        valueBottom = valueOff;
		    }
		    else {
		        valueTop = valueOff;
		        valueBottom = valueOn;
		    }
		    String gnucap0="", gnucap1="";
		    //# top resistor
		    if (node0.isWired() && node1.isWired()) {
		        String gnucap_resp = this.toGnucapRes(fullGnucapName+"_top", node0, node1,valueTop);
		        String values[]=gnucap_resp.split(kGnuCapDelimiter);
		        gnucap0+=values[0];
		        gnucap1+=values[1];
		    }
		    if (node0.isWired() && node2.isWired()) {
		        String gnucap_resp = this.toGnucapRes(fullGnucapName+"_bot", node0, node2,valueBottom);
		        String values[]=gnucap_resp.split(kGnuCapDelimiter);
		        gnucap0+=values[0];
		        gnucap1+=values[1];
		    }
		    return gnucap0 + kGnuCapDelimiter + gnucap1;
		}
		
	}
	private class Bulb extends ElectricComponent {
	    //private String internalResister;
	    private String resisterBlown;
	    
	    private float powerMax;
	    private boolean  isBlown;
		
	    private Bulb(float powerMax_) {
	        //138*172
	        super("bulb1.png", "R","1000");//internal resister, same as the const defined below

            super.getAnnotation().setColor(ccColor3B.ccRED);
            super.setAnnotationOffset(-4, 8);

            powerMax=powerMax_;
            
            ElectricNode nodeA=new ElectricNode("A",26.0f/138,0);
            super.addNode(nodeA);

            ElectricNode nodeB=new ElectricNode("B", 102.0f/138, 0);
            super.addNode(nodeB);

            //internalResister="1000";
            resisterBlown="100000000";
            isBlown=false;
	    }
	    /*
	    # Change the pixmap depending on the real power in the Bulb
	    */
	    public void setVoltageIntensity(boolean validValue_, float voltage_, float intensity_) {
	        super.setVoltageIntensity(validValue_,voltage_,intensity_);
	        if (!isBlown) {
	            float power = super.getVoltage() * super.getIntensity();
	            if (power < 0)
	                power = 0 - power;
	            int imgIndex=(int)(power * 10 / powerMax + 1);
	            if (imgIndex > 11)
	                imgIndex=11;
	            super.replaceTextureWith("bulb"+imgIndex+".png");
	                
	            //# If the Bulb is blown, we have to change it's internal
	            //# Resistor value to infinite and ask for a circuit recalc
	            if (imgIndex >= 11){
	                super.setGnucapValue(resisterBlown);
	                isBlown=true;
	            }
	        }
	    }
	}
	
	private class Rheostat extends ElectricComponent {
	    private int resistanceValue;
	    private int gnucap_current_resistor, gnucap_nb_resistor;

	    private CCSprite spriteWiper, spriteWiperWire;
	    private int minOffset,maxOffset;
		
	    private Rheostat(String resistance_) {
	        //35x139
	        super("resistor_track.png", "R", resistance_);

            resistanceValue=Integer.parseInt(resistance_);
            
            ElectricNode nodeA=new ElectricNode("A", 0.5f, 1.0f);
            super.addNode(nodeA);

            ElectricNode nodeB=new ElectricNode("B", 2.0f, 0.5f);
            super.addNode(nodeB);

            ElectricNode nodeC=new ElectricNode("C", 0.5f, 0);
            super.addNode(nodeC);
            
            spriteWiper=spriteFromExpansionFile("image/activities/experience/electric/resistor_wiper.png");
            spriteWiperWire=spriteFromExpansionFile("image/activities/experience/electric/wire.png");
            spriteWiperWire.setColor(ccColor3B.ccBLACK);
            
            gnucap_current_resistor = 1;
            gnucap_nb_resistor = 0;
	    }
	    //override
	    public void show(YDLayerBase parent) {
	        super.show(parent);
	        spriteWiper.setScale(super.getScale());
	        float hParent=super.getContentSize().height * super.getScale();
	        float hThis=spriteWiper.getContentSize().height*spriteWiper.getScale();
	        //bottom, relative to parent's bottom
	        minOffset=(int)(24.0f/139*hParent+hThis/2);
	        //top
	        maxOffset=(int)(114.0f/139*hParent-hThis/2);
	        
	        float x=super.getPosition().x;
	        float y=super.getPosition().y-hParent/2+maxOffset;
	        spriteWiper.setPosition(x,y);
	        parent.addChild(spriteWiper, 3);
	        this.updateWipeWire();
	        parent.addChild(spriteWiperWire, 3);
	    }
	    //Override
	    public void move(float xOffset, float yOffset) {
	        super.move(xOffset,  yOffset);
	        CGPoint pt=spriteWiper.getPosition();
	        pt.x += xOffset;
	        pt.y += yOffset;
	        spriteWiper.setPosition(pt);
	        this.updateWipeWire();
	    }
	    //overide
	    public void remove() {
	        super.remove();
	        spriteWiper.removeFromParentAndCleanup(true);
	        spriteWiperWire.removeFromParentAndCleanup(true);
	    }

	    //Override
	    public boolean isConnected() {
	        //The rheostat needs at least 2 connected nodes
	        int count=0;
	        for (ElectricNode node : super.getNodes()) {
	            if (node.isWired()) {
	                ++count;
	            }
	        }
	        return count >= 2;
	    }

	    // Return the gnucap definition for a single resitor of the rheostat
	    // node_id1 and node_id2 are the index in the list of nodes
	    private String toGnucapRes(String gnucapName, ElectricNode node1, ElectricNode node2, int gnucapValue) {
	        String gnucap = gnucapName + " ";
	        ElectricNode _nodes[]={node1, node2};
	        for (int i = 0; i < 2; ++i){
	            ElectricNode node=_nodes[i];
	            Wire first=node.getWires().get(0);
	            gnucap += String.format("%d ", first.getWireId());
	        }
	        gnucap += ""+gnucapValue+"\n";
	        
	        return gnucap+String.format("%s.print dc + v(%s) i(%s)\n", kGnuCapDelimiter, gnucapName, gnucapName);
	    }

	    //Override
	    public String toGnucap(String model) {
	        String fullGnucapName=super.getFullGnucapName();
	        
	        //reset set_voltage_intensity counter
	        gnucap_current_resistor = 0;
	        float y0=super.getPosition().y-super.getContentSize().height*super.getScale()/2;
	        float range=maxOffset-minOffset;
	        int gnucap_value=(int)(resistanceValue * (range - (spriteWiper.getPosition().y-y0 - minOffset))/range);

	        //# Main resistor
	        ElectricNode node0=super.getNodes().get(0);
	        ElectricNode node1=super.getNodes().get(1);
	        ElectricNode node2=super.getNodes().get(2);
	        
	        if (node0.isWired() && !node1.isWired() && node2.isWired()) {
	            gnucap_nb_resistor = 1;
	            String gnucap_resp = this.toGnucapRes(fullGnucapName+"_all", node0, node2, resistanceValue);
	            return gnucap_resp;
	        }
	        gnucap_nb_resistor=0;
	        String gnucap0="", gnucap1="";
	        if (node0.isWired() && node1.isWired()) {
	            gnucap_nb_resistor += 1;
	            String gnucap_resp  = this.toGnucapRes(fullGnucapName+"_top", node0, node1,gnucap_value);
	            String values[]=gnucap_resp.split(kGnuCapDelimiter);
	            gnucap0=values[0];
	            gnucap1=values[1];
	        }
	        
	        //bottom resistor
	        if (node1.isWired() && node2.isWired()) {
	            gnucap_nb_resistor += 1;
	            String gnucap_resp  = this.toGnucapRes(fullGnucapName+"_bot", node1,node2, resistanceValue-gnucap_value);
	            String values[]=gnucap_resp.split(kGnuCapDelimiter);
	            gnucap0=values[0];
	            gnucap1=values[1];
	        }
	        return gnucap0 + kGnuCapDelimiter + gnucap1;
	    }

	    // Return False if we need more value to complete our component
	    // This is usefull in case one Component is made of several gnucap component
	    public void setVoltageIntensity(boolean validValue_, float voltage_, float intensity_) {
	        gnucap_current_resistor += 1;
	        if (gnucap_current_resistor == 1) {
	            super.setVoltageIntensity(validValue_, voltage_, intensity_);
	        }
	        if (gnucap_nb_resistor != 1) {
	            gnucap_current_resistor += 1;
	            if (gnucap_current_resistor > gnucap_nb_resistor) {
	                gnucap_current_resistor = 0;
	            }
	        }
	        else {
	            gnucap_current_resistor = 0;
	        }
	    }

	    //Override
	    public boolean onClick(CGPoint ptClicked) {
	        CGPoint pt=spriteWiper.getPosition();
	        float y0=super.getPosition().y-super.getContentSize().height*super.getScale()/2;
	        if (ptClicked.y > super.getPosition().y) {
	            //move up
	            pt.y += 2 * super.getScale();
	            if (pt.y >y0 +maxOffset) {
	                pt.y =y0+maxOffset;
	            }
	        }
	        else {
	            //move down
	            pt.y -= 2 * super.getScale();
	            if (pt.y < y0+minOffset) {
	                pt.y = y0+minOffset;
	            }
	        }
	        spriteWiper.setPosition(pt);
	        this.updateWipeWire();
	        
	        return true;
	    }

	    //update the wipe wire position
	    private void updateWipeWire() {
	        CGPoint dst=spriteWiper.getPosition();
	        CGPoint src=CGPoint.ccp(0,0);
	        for (ElectricNode node : super.getNodes()) {
	            if (node.getName().equals("B")) {
	                src=node.getPosition();
	            }
	        }
	        
	        float xDiff=dst.x-src.x;
	        float yDiff=dst.y-src.y;
	        float distance=FloatMath.sqrt(xDiff*xDiff+yDiff*yDiff);
	        spriteWiperWire.setScaleX(distance/spriteWiperWire.getContentSize().width);
	        spriteWiperWire.setScaleY(2.0f/spriteWiperWire.getContentSize().height);
	        
	        spriteWiperWire.setRotation(angleFromPoint(dst, src));
	        spriteWiperWire.setPosition((dst.x+src.x)/2, (dst.y+src.y)/2);
	    }
	}
	private class EConnection extends ElectricComponent {
		private EConnection() {
			super ("connect_spot.png", "", "");
	        super.showAnnotation(false);
	        
	        ElectricNode nodeA=new ElectricNode("A", 0.28f, 0.5f);
	        super.addNode(nodeA);
		}

		//Override
		public boolean isConnected() {
		    return false;
		}
	}
	private class Resistor extends ElectricComponent {
		private Resistor(String resistance_) {
			super("resistor.png", "R", resistance_);

	        ElectricNode nodeA=new ElectricNode("A", 0, 0.5f);
	        super.addNode(nodeA);
	        
	        ElectricNode nodeB=new ElectricNode("B", 1, 0.5f);
	        super.addNode(nodeB);
		}
	}
	
	private class Diode extends ElectricComponent {
		private Diode() {
			super("diode.png", "D", "ddd 1.");

	        super.showAnnotation(false);

	        ElectricNode nodeA=new ElectricNode("A", 0, 0.5f);
	        super.addNode(nodeA);
	        
	        ElectricNode nodeB=new ElectricNode("B", 1, 0.5f);
	        super.addNode(nodeB);
		}

		//Override
		public String toGnucap(String model) {
		//# Our 'ddd' Diode model
		//# Idealized diode: ~0V treshold voltage. Characteristic graph
		//# passes through the two points (10 mV, 10 mA) and (20 mV, 2000
		//# mA) => N  = 0.072 IS = 5x10-5 A
		    String _model = ".model  ddd  d  ( is= 50.u  rs= 0.  n= 0.072  tt= 0.  cjo= 1.p  vj= 1.  m= 0.5 eg= 1.11  xti= 3.  kf= 0.  af= 1.  fc= 0.5  bv= 0.  ibv= 0.001 )\n";
		    
		    return super.toGnucap(_model);
		}

	}
	
	private class RedLed extends ElectricComponent {
		private RedLed () {
			super("red_led_off.png","D","led1 1.");

	        super.showAnnotation(false);

	        ElectricNode nodeA=new ElectricNode("A", 10.0f/80, 0);
	        super.addNode(nodeA);
	        
	        ElectricNode nodeB=new ElectricNode("B", 1, 0);
	        super.addNode(nodeB);
	    }
		
		//Override
		public String toGnucap(String model) {
		    String _model = ".model led1 d ( is=93.p rs=42M n=4.61 bv=4 ibv=10U cjo=2.97P vj=.75 M=.333 TT=4.32U)\n";
		    
		    return super.toGnucap(_model);
		}

		public void setVoltageIntensity(boolean validValue_, float voltage_, float intensity_){
		    super.setVoltageIntensity(validValue_,voltage_,intensity_);
		    float power = voltage_ * intensity_;
		    if (power > 0.01f) {
		        super.replaceTextureWith("red_led_on.png");
		    }
		    else {
		        super.replaceTextureWith("red_led_off.png");
		    }
		}
	}
	

	private final int kTagTooltip             =10;

	private final int kTooltipSelect          =1;
	private final int kTooltipDel             =2;
	private final int kTooltipWire            =3;
	private final int kTooltipBattery         =4;
	private final int kTooltipBulb            =5;
	private final int kTooltipRheostat        =6;
	private final int kTooltipResistor        =7;
	private final int kTooltipSwitch          =8;
	private final int kTooltipSwitch2         =9;
	private final int kTooltipConnection      =10;
	private final int kTooltipRedLed          =11;
	private final int kTooltipDiode           =12;
	
    private float tooltipsCanvasWidth;
    private CCMenuItemSprite selectedTooltipMenuitem, selMenuitem, delMenuitem;
    private int selectedTooltip;
    
    private java.util.ArrayList<ElectricComponent> electricComponents;
    private ElectricComponent picked;
    private ElectricNode src, dst;
    private CGPoint ptOrg, ptLast;
    private boolean selectionMoved;
    
    private float delta;
    
    private boolean modified;
    private String theLocker="____Locker___";
	
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new ElectricScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}
    
	public void onEnter() {
	    super.onEnter();
	    mMaxLevel=3;
	    Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	    super.shufflePlayBackgroundMusic();
	    super.setupBackground(activeCategory.getBg(),kBgModeFit);
	    super.setupTitle(activeCategory);
	    super.setupNavBar(activeCategory);
	    super.setupSideToolbar(activeCategory, kOptionIntr|kOptionHelp|kOptionLevelButtons|kOptionRepeatButton);
	    
	    tooltipsCanvasWidth=szWin.width/6;
	    
	    //tooltips background
	    CCSprite bgSprite=spriteFromExpansionFile("image/misc/layermaskwhite.png");
	    bgSprite.setColor(ccColor3B.ccc3(0xFF,0xD7,0x00));
	    bgSprite.setScaleX(tooltipsCanvasWidth/bgSprite.getContentSize().width);
	    bgSprite.setScaleY((szWin.height-topOverhead())/bgSprite.getContentSize().height);
	    bgSprite.setPosition(tooltipsCanvasWidth/2, szWin.height-topOverhead()-bgSprite.getContentSize().height*bgSprite.getScaleY()/2);
	    super.addChild(bgSprite, 1);
	    
	    electricComponents=new java.util.ArrayList<ElectricComponent>();
	    super.setIsTouchEnabled(true);
	    super.scheduleUpdate();
	    super.afterEnter();
	}	

	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);

		synchronized(this.theLocker) {
		    
		    super.clearFloatingSprites();
		    
		    for (ElectricComponent component : electricComponents) {
		        component.remove();
		    }
		    electricComponents.clear();
		    
		    if (mLevel == 1) {
		        int tools[]={kTooltipSelect, kTooltipDel, kTooltipBattery, kTooltipBulb, kTooltipSwitch};
		        this.setupTooltips(firstTime, tools, tools.length);
		    }
		    else if (mLevel == 2) {
		        int tools[]={kTooltipSelect, kTooltipDel, kTooltipBattery, kTooltipBulb, kTooltipRheostat, kTooltipSwitch, kTooltipSwitch2, kTooltipConnection};
		        this.setupTooltips(firstTime, tools, tools.length);
		    }
		    else if (mLevel == 3) {
		        int tools[]={kTooltipSelect, kTooltipDel, kTooltipBattery, kTooltipBulb, kTooltipRheostat,kTooltipResistor,
		             kTooltipDiode,kTooltipSwitch, kTooltipConnection}; //kTooltipDiode is similar as redled
		        this.setupTooltips(firstTime, tools, tools.length);
		    }
		    delta=0;
		    modified=false;
		}
	}


	private void setupTooltips(boolean firstTime, int []tooltips, int total){
	    selectedTooltipMenuitem=null;
	    
	    float margin=2*preferredContentScale(true);
	    float yTop=szWin.height-topOverhead() - margin;
	    
	    //in the same order as kTooltipXXX
	    String tools[]={"tool-select_on.png", "tool-del_on.png", "tool-line_on.png",
	                        "battery_icon.png", "bulb_icon.png",
	                        "resistor_track_icon.png", "resistor_icon.png",
	                        "switch_icon.png", "switch2_icon.png", "connect_icon.png",
	                        "red_led_icon.png", "diode_icon.png"};
	    float xPos=0, yPos=0;
        //level 3 has 10 elements
        float elementMaxHeight=(szWin.height-super.topOverhead())/(total-1);

	    for (int i = 0; i < total; ++i) {
	        if (tooltips[i] <= 0)
	            continue;
	        String str=tools[tooltips[i]-1];        
	        String img="image/activities/experience/electric/" + str;
	        CCSprite sprite=spriteFromExpansionFile(img);
	        if (i < 2) {//the first two are menu items
	            String imgSel=super.buttonize(img);
	            CCTexture2D texture=CCTextureCache.sharedTextureCache().addImageExternal(imgSel);
	            CCSprite spriteSelected=CCSprite.sprite(texture);
	            CCMenuItemSprite menuitem=CCMenuItemImage.item(sprite, spriteSelected,this,"tooltipTouched");
	            menuitem.setTag(kTagTooltip+tooltips[i]);
	            CCMenu menu = CCMenu.menu(menuitem);
	            float divided=tooltipsCanvasWidth/2;
	            if (divided > elementMaxHeight)
	            	divided=elementMaxHeight;
	            menuitem.setScale(divided/menuitem.getContentSize().width*0.85f);
	            xPos=tooltipsCanvasWidth *((i==0)?0.25f:0.75f);
	            yPos=yTop;
	            menu.setPosition(xPos, yPos - menuitem.getContentSize().height * menuitem.getScale()/2);
	            super.addChild(menu, 2);
	            floatingSprites.add(menu);
	            
	            if (tooltips[i]==kTooltipSelect)
	                selMenuitem=menuitem;
	            else if (tooltips[i]==kTooltipDel)
	                delMenuitem=menuitem;
	            
	            sprite.setColor((selectedTooltip==tooltips[i])?ccColor3B.ccWHITE:ccColor3B.ccGRAY);
	            if (i >= 1)
	                yPos -= menuitem.getContentSize().height*menuitem.getScale()+margin;
	        }
	        else {
	            float maxWidth=tooltipsCanvasWidth*0.85f;
	            float scale1=maxWidth/sprite.getContentSize().width;
	            float scale2=elementMaxHeight/sprite.getContentSize().height;
	            float scale=(scale2>scale1)?scale1:scale2;
	            sprite.setTag(kTagTooltip+tooltips[i]);
	            sprite.setScale(scale);
	            xPos=tooltipsCanvasWidth/2;
	            sprite.setPosition(xPos, yPos - sprite.getContentSize().height * sprite.getScale()/2);
	            
	            super.addChild(sprite, 2);
	            floatingSprites.add(sprite);
	            
	            yPos -= sprite.getContentSize().height*sprite.getScale()+margin;
	        }
	    }
	    this.restore2DefaultTooltip();
	}

	private void restore2DefaultTooltip() {
	    selectedTooltip=kTooltipSelect;
	    
	    selectedTooltipMenuitem=selMenuitem;
	    selMenuitem.setColor(ccColor3B.ccWHITE);
	    delMenuitem.setColor(ccColor3B.ccGRAY);
	}

	public void tooltipTouched(Object _sender) {
		CCNode sender=(CCNode)_sender;
	    selectedTooltipMenuitem.setColor(ccColor3B.ccGRAY);
	    selectedTooltipMenuitem=(CCMenuItemSprite)sender;
	    selectedTooltipMenuitem.setColor(ccColor3B.ccWHITE);
	    selectedTooltip=sender.getTag()-kTagTooltip;
	    if (selectedTooltip==kTooltipSelect)
	        super.playSound("audio/sounds/bleep.wav");
	    else
	        super.playSound("audio/sounds/eraser1.wav");
	}

	private void popupPrompt(int tooltip, CGPoint pt) {
	     String key="electric_tool_" + tooltip;
	     String tips=localizedString(key);
	     
        CCLabel label=CCLabel.makeLabel(tips, super.sysFontName(), super.smallFontSize()-2);
        label.setPosition(pt);
        label.setColor(ccColor3B.ccWHITE);
        super.addChild(label, 102);
        
        CCMoveTo moveAction=CCMoveTo.action(1.5f, CGPoint.ccp(0-label.getContentSize().width, label.getPosition().y+label.getContentSize().height*3));
        CCCallFuncN doneAction = CCCallFuncN.action(this, "removeMe");
        label.runAction(CCSequence.actions(moveAction, doneAction));
	}

	public void removeMe(Object _sender) {
		CCNode sender=(CCNode)_sender;
	    sender.removeFromParentAndCleanup(true);
	}

	public boolean ccTouchesBegan(MotionEvent event) {
		synchronized(this.theLocker) {
			CGPoint pt = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));
	        picked=null; src=null; selectionMoved=false;
	        boolean _transient=false;
	        if (pt.x < tooltipsCanvasWidth) {
	            for (CCNode node : floatingSprites) {
	                if (super.isNodeHit(node,  pt) && node.getTag() >= kTagTooltip+kTooltipWire) {
	                    selectedTooltip=node.getTag()-kTagTooltip;
	                    this.popupPrompt(selectedTooltip, node.getPosition());
	                    _transient=true;
	                    break;
	                }
	            }
	        }
	        ptOrg=ptLast=pt;
	        switch (selectedTooltip) {
	            case kTooltipSelect:
	                for (ElectricComponent component : electricComponents) {
	                    for (ElectricNode node : component.getNodes()) {
	                        if (node.hit(pt)) {
	                            src=node;
	                            break;
	                        }
	                    }
	                    if ((src==null) && component.hit(pt)) {
	                        picked=component;
	                        break;
	                    }
	                }
	                if (src!=null) {
	                    selectedTooltip=kTooltipWire;
	                    picked=null;
	                }
	                else if (picked!=null) {
	                    if (picked instanceof Wire) {
	                        picked=null;
	                    }
	                    else {
	                        ptLast=pt;
	                    }
	                }
	                break;
	            case kTooltipDel:
	            {
	                CCSprite sparkle=spriteFromExpansionFile("image/misc/star.png");
	                sparkle.setPosition(pt);
	                sparkle.setScale(2.0f);
	                sparkle.setColor(ccColor3B.ccRED);
	                super.addChild(sparkle, 100);
	                CCScaleTo scaleDownAction=CCScaleTo.action(0.4f, 0.2f);
	                CCCallFuncN actionDone = CCCallFuncN.action(this, "removeMe");
	                sparkle.runAction(CCSequence.actions(scaleDownAction, actionDone));
	            }
	                break;
	            case kTooltipBattery:
	            {
	                Battery battery=new Battery("10");
	                battery.setPosition(pt);
	                electricComponents.add(battery);
	            }
	                break;
	            case kTooltipBulb:
	            {
	                Bulb bulb=new Bulb(0.11f);
	                bulb.setPosition(pt);
	                electricComponents.add(bulb);
	            }
	                break;
	            case kTooltipSwitch:
	            {
	                Switch swch=new Switch();
	                swch.setPosition(pt);
	                electricComponents.add(swch);
	            }
	                break;
	            case kTooltipRheostat:
	            {
	                Rheostat rheostat=new Rheostat("1000");
	                rheostat.setPosition(pt);
	                electricComponents.add(rheostat);
	            }
	                break;
	            case kTooltipResistor:
	            {
	                Resistor resistor=new Resistor("1000");
	                resistor.setPosition(pt);
	                electricComponents.add(resistor);
	            }
	                break;
	            case kTooltipSwitch2:
	            {
	                Switch2 swh=new Switch2();
	                swh.setPosition(pt);
	                electricComponents.add(swh);
	            }
	                break;
	            case kTooltipConnection:
	            {
	                EConnection cont =new EConnection();
	                cont.setPosition(pt);
	                electricComponents.add(cont);
	            }
	                break;
	            case kTooltipRedLed:
	            {
	                RedLed led=new RedLed();
	                led.setPosition(pt);
	                electricComponents.add(led);
	            }
	                break;
	            case kTooltipDiode:
	            {
	                Diode diode=new Diode();
	                diode.setPosition(pt);
	                electricComponents.add(diode);
	            }
	                break;
	        }
	        if (_transient) {
	            this.restore2DefaultTooltip();
	            picked=electricComponents.get(electricComponents.size()-1);
	            picked.setScale(preferredContentScale(true));
	            picked.show(this);
	        }
	    }
		return true;
	}
	public boolean ccTouchesMoved(MotionEvent event) {
		synchronized(this.theLocker) {
			CGPoint pt = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));
			selectionMoved=super.distanceFrom(pt, ptOrg) > 10;
	        switch (selectedTooltip) {
	            case kTooltipSelect:
	                if (picked!=null) {
	                    float xOffset=pt.x-ptLast.x;
	                    float yOffset=pt.y-ptLast.y;
	                    picked.move(xOffset, yOffset);
	                    ptLast=pt;
	                }
	                break;
	            case kTooltipDel:
	                break;
	            case kTooltipWire:
	                if (src!=null) {
	                    if (picked==null) {
	                        Wire wire=new Wire();
	                        wire.setSrc(src);
	                        dst=new ElectricNode("dummy",0,0);
	                        dst.setPosition(pt);
	                        wire.setDst(dst);
	                        wire.show(this);
	                        picked=wire;
	                    }
	                    dst.setPosition(pt);
	                    picked.move(0,  0);
	                }
	                break;
	            case kTooltipBattery:
	                break;
	            case kTooltipBulb:
	                break;
	            case kTooltipSwitch:
	                break;
	        }
	    }
		return true;
	}

	public boolean ccTouchesEnded(MotionEvent event) {
		synchronized(this.theLocker) {
			CGPoint pt = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));
	        switch (selectedTooltip) {
	            case kTooltipSelect:
	                if (picked!=null) {
	                    if (pt.x < tooltipsCanvasWidth) {
	                        this.removeComponent(picked);
	                        modified=true;
	                    }
	                    else if (!selectionMoved) {
	                        if (picked.onClick(pt))
	                            modified=true;
	                    }
	                }
	                break;
	            case kTooltipDel:
	                picked=null;
	                for (ElectricComponent component : electricComponents) {
	                    if (component.hit(pt)) {
	                        picked=component;
	                        break;
	                    }
	                }
	                if (picked!=null) {
	                    this.removeComponent(picked);
	                    picked=null;
	                    modified=true;
	                }
	                break;
	            case kTooltipWire:
	                if (src!=null && picked!=null) {
	                    ElectricNode connect2=null;
	                    for (ElectricComponent component : electricComponents) {
	                        for (ElectricNode node : component.getNodes()) {
	                            if (node.hit(pt)) {
	                                connect2=node;
	                                break;
	                            }
	                        }
	                    }
	                    Wire wire=(Wire)picked;
	                    if (connect2!=null && connect2 != src && connect2.getComponent() != src.getComponent()) {
	                        //any existing wire between these two nodes
	                        boolean find=false;
	                        for (Wire one : src.getWires()) {
	                            for (Wire another : connect2.getWires()) {
	                                if (another == one) {
	                                    find=true;
	                                    break;
	                                }
	                            }
	                            if (find)
	                                break;
	                        }
	                        if (find) {
	                            picked.remove();
	                        }
	                        else {
	                            src.addWire(wire);
	                            connect2.addWire(wire);

	                            wire.setDst(connect2);
	                            wire.move(0, 0);
	                            electricComponents.add(wire);
	                            modified=true;
	                        }
	                    }
	                    else {
	                        picked.remove();
	                    }
	                }
	                if (dst!=null) {
	                    dst=null;
	                }
	                if (picked!=null) {
	                    picked=null;
	                }
	                this.restore2DefaultTooltip();
	                break;
	        }
		    picked=null;
	    }
		return true;
	}

	private void removeComponent(ElectricComponent theComponent){
	    theComponent.remove();
	    electricComponents.remove(theComponent);
	    //any related wire is removed?
	    int total=electricComponents.size();
	    for (int i = total - 1; i >= 0; --i) {
	        ElectricComponent component=electricComponents.get(i);
	        if (component instanceof Wire) {
	            Wire wire=(Wire)component;
	            if (wire.isRemoved()) {
	                electricComponents.remove(i);
	            }
	        }
	    }
	}

	public void update(float dt) {
	    delta += dt;
	    if (delta < 2) //update every two seconds
	        return;
	    delta=0;
	    synchronized(theLocker) {
		    if (electricComponents.size() > 0)
		        this.runSimulation();
		}
	}

	public void repeat(Object sender){
	    this.initGame(false, sender);
	}

	 //native method
	//extern int gnucap_main(int argc, const char *argv[]);

	private void runSimulation() {
	    if (!modified)
	        return;
	    modified=false;
	    
	    ElectricComponent first=electricComponents.get(0);
	    first.normalize(electricComponents);
	    for (ElectricComponent component : electricComponents){
	        component.setVoltageIntensity(false, 0, 0);
	        if (component instanceof Wire) {
	        }
	    }
	    
	    String gnucap = "Title YellowDuck\n";
	//# Ugly hack: connect a 0 ohm (1 fempto) resistor between net 0
	//# and first net found
	    gnucap = gnucap + "R999999999 0 ";
	    boolean found = false;
	    for (ElectricComponent component : electricComponents){
	        if (component.isConnected()) {
	            for (ElectricNode node : component.getNodes()) {
	                if (node.getWires().size() > 0) {
	                    Wire _first=node.getWires().get(0);
	                    gnucap = gnucap + ""+ _first.getWireId();
	                    found = true;
	                    break;
	                }
	            }
	            if (found)
	                break;
	        }
	    }
	    gnucap = gnucap  + " 1f\n";
	    
	    int connectedComponents=0;
	    String gnucap_print = "";
	    for (ElectricComponent component : electricComponents){
	        if (component.isConnected()) {
	            ++connectedComponents;
	            String thisgnucap = component.toGnucap(null);
	            String data[]=thisgnucap.split(kGnuCapDelimiter);
	            gnucap = gnucap  + data[0];
	            gnucap_print = gnucap_print  + data[1];
	        }
	    }
	    gnucap = gnucap + gnucap_print;
	    gnucap = gnucap + ".dc\n";
	    gnucap = gnucap + ".end\n";
	    //android.util.Log.e("CAP", gnucap);
	    //NSLog(@"%@\n--------------", gnucap);

	    if (connectedComponents <= 1)
	        return;
	    
	    //write to temporary file
	    String filename=YDConfiguration.context.getCacheDir() + java.io.File.separator +"electric.gnucap";
	    String outputfile=filename+".out";
	    //make sure the output file does not exit yet
	    //[[NSFileManager defaultManager] removeItemAtPath:outputfile error:nil];
	    
	    boolean err=true;
	    try {
	    	FileWriter writer=new FileWriter(filename);
	    	writer.write(gnucap);
	    	writer.close();
	    	err=false;
    	} catch (Exception e) {
    	  e.printStackTrace();
    	}
	    if (err) {
	        //NSLog(@"%@", [err localizedDescription]);
	    }
	    else {
	    	callGnuCap(filename);
	    	java.util.ArrayList<String> lines=new java.util.ArrayList<String>();
	    	try {
		        BufferedReader reader = new BufferedReader( new FileReader (outputfile));
		        String         line = null;
		        while( ( line = reader.readLine() ) != null ) {
		        	//android.util.Log.e("CAPRET", line);
		        	lines.add(line);
		        }
		        reader.close();
	    	}
	    	catch (Throwable ignore) {
	    	}	    	
	        String line=null, title=null;
	        for (String _line : lines) {
	            if (_line.startsWith(" 0.")) {
	                line=_line;
	                break;
	            }
	            else if (_line.startsWith("#")) {
	                title=_line;
	            }
	        }
	        if (line!=null && title!=null) {
	            java.util.ArrayList<String> titles=new java.util.ArrayList<String>();
	            String ts[]=title.split(" ");
	            for (int i = 0; i < ts.length; ++i) {
	            	String one=ts[i].trim();	
	                if (one.length() > 0 && !one.equals("#"))
	                    titles.add(one);
	            }
	            java.util.ArrayList<String> values=new java.util.ArrayList<String>(); 
	            boolean firstCol=true;
	            String ls[]=line.split(" ");
	            for (int l=0;l<ls.length;++l) {
	            	String one=ls[l].trim();
	                if (one.length() > 0) {
	                    if (firstCol) {
	                        firstCol=false;
	                    }
	                    else {
	                        values.add(one);
	                    }
	                }
	            }
	            int total=values.size();
	            for (int idx=0; idx < total; idx += 2) {
	                float volt = this.convertGnucapValue(values.get(idx));
	                float amp = this.convertGnucapValue(values.get(idx+1));
	                //get the component name from the title
	                String theTitle=titles.get(idx);
	                int find1=theTitle.indexOf("(");
	                int find2=theTitle.indexOf(")");
	                String componentName=(find2 > find1 && find1 > 0) ? theTitle.substring(find1+1,  find2):"";
	                int find=componentName.indexOf("_");
	                if (find >0) {
	                    componentName=componentName.substring(0, find);
	                }
	                //find the component with given name
	                ElectricComponent theComponent=null;
	                for (ElectricComponent component : electricComponents){
	                    String gnucapName=component.getFullGnucapName();
	                    if (gnucapName.equals(componentName)) {
	                        theComponent=component;
	                        break;
	                    }
	                }
	                if (theComponent!=null)
	                    theComponent.setVoltageIntensity(true, volt, amp);
	            }
	        }
	    }
	    new java.io.File(filename).delete();
	    new java.io.File(outputfile).delete();
	}



	// Convert a gnucap value back in a regular number
	// Return a float value
	// Or a ValueError exception
	private float convertGnucapValue(String value) {
	    double unit = 1;
	    if (value.endsWith("T")) {
	        unit = 1e12;
	        value = value.replaceAll("T", "");
	    }
	    if (value.endsWith("G")) {
	        unit = 1e9;
	        value = value.replaceAll("G","");
	    }
	    if (value.endsWith("Meg")) {
	        unit = 1e6;
	        value = value.replaceAll("Meg","");
	    }
	    if (value.endsWith("K")) {
	        unit = 1e3;
	        value = value.replaceAll("K","");
	    }
	    if (value.endsWith("u")) {
	        unit = 1e-6;
	        value = value.replaceAll("u","");
	    }
	    if (value.endsWith("n")) {
	        unit = 1e-9;
	        value = value.replaceAll("n","");
	    }
	    if (value.endsWith("p")) {
	        unit = 1e-12;
	        value = value.replaceAll("p","");
	    }
	    if (value.endsWith("f")) {
	        unit = 1e-15;
	        value = value.replaceAll("f","");
	    }
	    if (value.endsWith("f")) {
	        unit = 1e-15;
	        value = value.replaceAll("f","");
	    }
	    float floatValue=Float.parseFloat(value);
	    float sign=(floatValue>0)?1:-1;

	    //# return absolue value
	    return (float)(floatValue * unit *sign);
	}
}
