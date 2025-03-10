abstract class Expr {
  abstract <R> R accept(Visitor<R> v);
	interface Visitor<R> {
		R visitBinary(Binary binary);
		R visitGrouping(Grouping grouping);
		R visitLiteral(Literal literal);
		R visitUnary(Unary unary);
	}
	static class Binary extends Expr {
		Binary( Expr left, LoxToken operator, Expr right){
			this.left=left;
			this.operator=operator;
			this.right=right;
		}
		@Override
		<R> R accept(Visitor<R> v) {
			return v.visitBinary(this);
		}
		final Expr left;
		final LoxToken operator;
		final Expr right;
	}
	static class Grouping extends Expr {
		Grouping( Expr expression){
			this.expression=expression;
		}
		@Override
		<R> R accept(Visitor<R> v) {
			return v.visitGrouping(this);
		}
		final Expr expression;
	}
	static class Literal extends Expr {
		Literal( Object value){
			this.value=value;
		}
		@Override
		<R> R accept(Visitor<R> v) {
			return v.visitLiteral(this);
		}
		final Object value;
	}
	static class Unary extends Expr {
		Unary( LoxToken operator, Expr right){
			this.operator=operator;
			this.right=right;
		}
		@Override
		<R> R accept(Visitor<R> v) {
			return v.visitUnary(this);
		}
		final LoxToken operator;
		final Expr right;
	}
}
