package aadl2upaal.aadl;
import aadl2upaal.visitor.NodeVisitor;

//component implement
public class CompImpl extends ANode
{
   public CompImpl(String name) { super(name); }

   public void accept(NodeVisitor visitor)  {
//      visitor.processCompImpl(this);
   }

   public boolean equals(Object o) {
      if(!(o instanceof CompImpl)) return false;
      CompImpl c = (CompImpl) o;
      return name.equals(c.name);
   }
}