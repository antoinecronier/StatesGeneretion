package com.tactfactory.statesgeneration.database;

public class RequestBuilder {

	Select composite;

	public Table select(String selection){
		this.composite = new Select(this);
		return this.composite.select(selection);
	}

	public String build(){
		StringBuilder result = new StringBuilder();

		RequestComposite compo = composite;
		while (compo != null) {
			if (compo instanceof Select) {
				result.append("SELECT ");
				result.append(compo.getElement());
			}else if(compo instanceof Table){
				result.append(" FROM ");
				result.append(compo.getElement());
				result.append(" WHERE");
			}else {
				if (compo.getElement() != null) {
					result.append(" " + compo.getElement());
				}
			}
			compo = compo.getComposite();
		}
		return result.toString();
	}

	public class Select implements RequestComposite{

		Table composite;
		String element;
		RequestBuilder builder;

		public Select(RequestBuilder builder){
			this.builder = builder;
		}

		public Table select(String selection){
			this.element = selection;
			this.composite = new Table(builder);
			return this.composite;
		}

//		public String build(){
//			return this.builder.build();
//		}

		@Override
		public RequestComposite getComposite() {
			return this.composite;
		}

		@Override
		public String getElement() {
			return this.element;
		}
	}

	public class Table implements RequestComposite{

		Where composite;
		String element;
		RequestBuilder builder;

		public Table(RequestBuilder builder){
			this.builder = builder;
		}

		public Where table(String selection){
			this.composite = new Where(this.builder);
			this.element = selection;
			return this.composite;
		}

//		public String build(){
//			return this.builder.build();
//		}

		@Override
		public RequestComposite getComposite() {
			return this.composite;
		}

		@Override
		public String getElement() {
			return this.element;
		}
	}

	public class Where implements RequestComposite{

		Operator composite;
		String element;
		RequestBuilder builder;

		public Where(RequestBuilder builder){
			this.builder = builder;
		}

		public Operator where(String selection){
			this.composite = new Operator(builder);
			this.element = selection;
			return composite;
		}

		public String build(){
			return this.builder.build();
		}

		@Override
		public RequestComposite getComposite() {
			return this.composite;
		}

		@Override
		public String getElement() {
			return this.element;
		}
	}

	public class Operator implements RequestComposite{
		Where composite;
		String element;
		RequestBuilder builder;

		public Operator(RequestBuilder builder){
			this.builder = builder;
		}

		public Where and(){
			this.composite = new Where(this.builder);
			this.element = "and";
			return composite;
		}

		public Where or(){
			this.composite = new Where(this.builder);
			this.element = "or";
			return composite;
		}

		public String build(){
			return this.builder.build();
		}

		@Override
		public RequestComposite getComposite() {
			return this.composite;
		}

		@Override
		public String getElement() {
			return this.element;
		}
	}
}
