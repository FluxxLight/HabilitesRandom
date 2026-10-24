package com.habilitesrandom.combat;
import com.habilitesrandom.api.AreaShape;
/** Allocation-free shape checks usable by server executors and tests. */
public final class CombatGeometry { private CombatGeometry(){}
 public static boolean contains(AreaShape shape,double ox,double oy,double oz,double fx,double fz,double x,double y,double z,double range,double area){double dx=x-ox,dy=y-oy,dz=z-oz,d2=dx*dx+dy*dy+dz*dz;if(d2>range*range)return false;return switch(shape){case NONE->false;case CIRCLE,SELF,POINT->d2<=area*area;case BOX->Math.abs(dx)<=area&&Math.abs(dy)<=area&&Math.abs(dz)<=area;case LINE,BEAM->line(dx,dz,fx,fz,area,range);case CONE->{double len=Math.sqrt(dx*dx+dz*dz);yield len<=area&&(len==0||(dx*fx+dz*fz)/len>=.70);}};}
 private static boolean line(double x,double z,double fx,double fz,double width,double range){double along=x*fx+z*fz;if(along<0||along>range)return false;double side=Math.abs(x*fz-z*fx);return side<=Math.max(.5,width);}
}
