package corpus.maker;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class SourceVisitor extends VoidVisitorAdapter {

    public ArrayList<String> classNames;
    public ArrayList<String> methodNames;
    public ArrayList<String> methodcalls;
    public ArrayList<String> attributeNames;

    public SourceVisitor() {
        // initialization of items
        this.classNames = new ArrayList<>();
        this.methodNames = new ArrayList<>();
        this.methodcalls = new ArrayList<>();
        this.attributeNames = new ArrayList<>();
    }

    @Override
    public void visit(ImportDeclaration imp, Object arg) {
        // String[] parts = imp.getName().toString().split("\\.");
        // String className = parts[parts.length - 1];
        // adding the class name
        this.classNames.add(imp.getName().toString());
    }

    @Override
    public void visit(MethodCallExpr mcall, Object args) {
        String methodcall = mcall.getName();
        String scope = mcall.getScope().toString();
        try {
            this.methodcalls.add(methodcall);
            this.attributeNames.add(scope);
        } catch (Exception e) {
        }
    }

    @Override
    public void visit(FieldAccessExpr faExpr, Object args) {
        String fieldName = faExpr.getField();
        this.attributeNames.add(fieldName);
        try {
            Expression fScope = faExpr.getScope();
            fScope.accept(this, args);
        } catch (Exception e) {
        }
    }

    @Override
    public void visit(VariableDeclarationExpr vdExp, Object args) {
        String type = vdExp.getType().toString();
        this.classNames.add(type);
        try {
            List<VariableDeclarator> varNames = vdExp.getVars();
            for (VariableDeclarator vdec : varNames) {
                this.attributeNames.add(vdec.getId().toString());
            }
        } catch (Exception exc) {
            // handle the exception
        }
    }

    @Override
    public void visit(ObjectCreationExpr objExp, Object args) {
        String typeName = objExp.getType().getName();
        this.classNames.add(typeName);
        try {
            List<Expression> classArgs = objExp.getArgs();
            for (Expression expr : classArgs) {
                expr.accept(this, args);
            }
            List<Type> typeArgs = objExp.getTypeArgs();
            for (Type type : typeArgs) {
                type.accept(this, args);
            }
        } catch (Exception exc) {
            // handle the exception
        }
    }

    @Override
    public void visit(FieldDeclaration fdec, Object arg) {
        List<VariableDeclarator> fieldNames = fdec.getVariables();
        this.classNames.add(fdec.getType().toString());
        try {
            for (VariableDeclarator vdec : fieldNames) {
                this.attributeNames.add(vdec.getId().toString());
            }
        } catch (Exception exc) {
            // handle the exception
        }
    }

    @Override
    public void visit(MethodDeclaration m, Object arg) {
        String methodName = m.getName();
        this.methodNames.add(methodName);
        // browsing statements
        try {
            BlockStmt body = m.getBody();
            // visiting body
            List<Statement> stmts = body.getStmts();
            for (Statement stmt : stmts) {
                stmt.accept(this, arg);
            }
        } catch (Exception exc) {
        }
    }

    @Override
    public void visit(ConstructorDeclaration cd, Object arg) {
        String methodName = cd.getName();
        this.methodNames.add(methodName);
        // browsing statements
        try {
            BlockStmt body = cd.getBlock();
            // visiting body
            List<Statement> stmts = body.getStmts();
            for (Statement stmt : stmts) {
                stmt.accept(this, arg);
            }
        } catch (Exception exc) {
            // handle the exception
        }
    }
}
