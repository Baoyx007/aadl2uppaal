package aadl2upaal.aadl;
import aadl2upaal.visitor.NodeVisitor;
import aadl2upaal.visitor.UpaalWriter;

public class SubComp extends CompImpl
{
   public CompImpl context = null, classifier = null;

   public SubComp(String name) { super(name); }

   public void accept(NodeVisitor visitor) {
      visitor.processSubComp(this);
   }

   public boolean equals(Object o) {
      if(!(o instanceof SubComp)) return false;
      SubComp s = (SubComp) o;
      if(!context.equals(s.context)) return false;
      if(!classifier.equals(s.classifier)) return false;
      return name.equals(s.name);
   }
   
   public CompImpl convert2Impl(){
	   return new CompImpl(this.name);
   }

   public String fullName() {
      return context.toString() + UpaalWriter.sep + name;
   }

   public String upaalName() {
      return context.name + UpaalWriter.sep + name;
   }

   public String toString() { return fullName(); }
}