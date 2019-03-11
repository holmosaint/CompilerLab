//
// Generated by JTB 1.3.2
//

package syntaxtree;

/**
 * Grammar production:
 * f0 -> "if"
 * f1 -> "("
 * f2 -> Expression()
 * f3 -> ")"
 * f4 -> Statement()
 * f5 -> "else"
 * f6 -> Statement()
 */
public class IfStatement implements Node {
   public NodeToken f0;
   public NodeToken f1;
   public Expression f2;
   public NodeToken f3;
   public Statement f4;
   public NodeToken f5;
   public Statement f6;

   public IfStatement(NodeToken n0, NodeToken n1, Expression n2, NodeToken n3, Statement n4, NodeToken n5, Statement n6) {
      f0 = n0;
      f1 = n1;
      f2 = n2;
      f3 = n3;
      f4 = n4;
      f5 = n5;
      f6 = n6;
   }

   public IfStatement(Expression n0, Statement n1, Statement n2) {
      f0 = new NodeToken("if");
      f1 = new NodeToken("(");
      f2 = n0;
      f3 = new NodeToken(")");
      f4 = n1;
      f5 = new NodeToken("else");
      f6 = n2;
   }

   public void accept(visitor.Visitor v) {
      v.visit(this);
   }
   public <R,A> R accept(visitor.GJVisitor<R,A> v, A argu) {
      return v.visit(this,argu);
   }
   public <R> R accept(visitor.GJNoArguVisitor<R> v) {
      return v.visit(this);
   }
   public <A> void accept(visitor.GJVoidVisitor<A> v, A argu) {
      v.visit(this,argu);
   }
}

