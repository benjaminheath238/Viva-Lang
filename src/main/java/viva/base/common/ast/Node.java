package viva.base.common.ast;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.ToString;
import viva.base.common.Environment;
import viva.base.common.Token;
import viva.utils.Couple;

public interface Node {
    public <T> T accept(NodeVisitor<T> visitor);

    @ToString
    @AllArgsConstructor
    public static class Program implements Node {
        public final Environment env;
        public final List<Node.Stmt> body;
        
        @Override
        public <T> T accept(NodeVisitor<T> visitor) { return visitor.visitProgram(this); }
    }

    public static interface Stmt extends Node {
        @ToString
        @AllArgsConstructor
        public static class Block implements Node.Stmt {
            public final Environment env;
            public final List<Node.Stmt> body;
        
            @Override 
            public <T> T accept(NodeVisitor<T> visitor) { return visitor.visitBlockStmt(this); }
        }

        @ToString
        @AllArgsConstructor
        public static class While implements Node.Stmt {
            public final Node.Expr condition;
            public final Node.Stmt body;

            @Override 
            public <T> T accept(NodeVisitor<T> visitor) { return visitor.visitWhileStmt(this); }
        }

        @ToString
        @AllArgsConstructor
        public static class Until implements Node.Stmt {
            public final Node.Expr condition;
            public final Node.Stmt body;

            @Override 
            public <T> T accept(NodeVisitor<T> visitor) { return visitor.visitUntilStmt(this); }
        }

        @ToString
        @AllArgsConstructor
        public static class If implements Node.Stmt {
            public final Node.Expr condition;
            public final Node.Stmt body;
            public final List<Couple<Node.Expr, Node.Stmt>> elifs;
            public final Node.Stmt elseBody;

            @Override 
            public <T> T accept(NodeVisitor<T> visitor) { return visitor.visitIfStmt(this); }
        }

        public static interface Define extends Node.Stmt {
            @ToString
            @AllArgsConstructor
            public static class Proc implements Node.Stmt.Define {
                public final Token identifier;
                public final Token returnType;
                public final Map<Token, Token> parameters;
                public final Node.Expr body;

                @Override 
                public <T> T accept(NodeVisitor<T> visitor) { return visitor.visitDefineProcStmt(this); }
            }
            
            @ToString
            @AllArgsConstructor
            public static class Type implements Node.Stmt.Define {
                public final String kind;
                public final Token identifier;
                public final Node.Expr body;

                @Override 
                public <T> T accept(NodeVisitor<T> visitor) { return visitor.visitDefineTypeStmt(this); }
            }

            @ToString
            @AllArgsConstructor
            public static class Variable implements Node.Stmt.Define {
                public final Token type;
                public final Token identifier;
                public final Node.Expr value;

                @Override 
                public <T> T accept(NodeVisitor<T> visitor) { return visitor.visitDefineVariableStmt(this); }
            }
        }

        @ToString
        @AllArgsConstructor
        public static class Return implements Node.Stmt {
            public final Node.Expr value;
            
            @Override 
            public <T> T accept(NodeVisitor<T> visitor) { return visitor.visitReturnStmt(this); }
        }

        @ToString
        @AllArgsConstructor
        public static class Continue implements Node.Stmt {
            public final Token identifier;

            @Override 
            public <T> T accept(NodeVisitor<T> visitor) { return visitor.visitContinueStmt(this); }
        }

        @ToString
        @AllArgsConstructor
        public static class Break implements Node.Stmt {
            public final Token identifier;

            @Override 
            public <T> T accept(NodeVisitor<T> visitor) { return visitor.visitBreakStmt(this); }
        }

        @ToString
        @AllArgsConstructor
        public static class Expr implements Node.Stmt {
            public final Node.Expr value;

            @Override 
            public <T> T accept(NodeVisitor<T> visitor) { return visitor.visitExprStmt(this); }
        }
    }

    public static interface Expr extends Node {
        @ToString
        @AllArgsConstructor
        public static class Binary implements Node.Expr {
            public final Node.Expr operandLeft;
            public final Token operator;
            public final Node.Expr operandRight;

            @Override 
            public <T> T accept(NodeVisitor<T> visitor) { return visitor.visitBinaryExpr(this); }
        }

        @ToString
        @AllArgsConstructor
        public static class Unary implements Node.Expr {
            public final Token operator;
            public final Node.Expr operandRight;

            @Override 
            public <T> T accept(NodeVisitor<T> visitor) { return visitor.visitUnaryExpr(this); }
        }
        
        @ToString
        @AllArgsConstructor
        public static class Literal implements Node.Expr {
            public final Token value;

            @Override 
            public <T> T accept(NodeVisitor<T> visitor) { return visitor.visitLiteralExpr(this); }
        }
        
        @ToString
        @AllArgsConstructor
        public static class Variable implements Node.Expr {
            public final Token identifier;

            @Override 
            public <T> T accept(NodeVisitor<T> visitor) { return visitor.visitVariableExpr(this); }
        }
        
        @ToString
        @AllArgsConstructor
        public static class ProcCall implements Node.Expr {
            public final Token identifier;
            public final List<Node.Expr> arguments;

            @Override 
            public <T> T accept(NodeVisitor<T> visitor) { return visitor.visitProcCallExpr(this); }
        }
        
        @ToString
        @AllArgsConstructor
        public static class Access implements Node.Expr {
            public final Token identifier;
            public final Node.Expr index;

            @Override 
            public <T> T accept(NodeVisitor<T> visitor) { return visitor.visitAccessExpr(this); }
        }
        
        @ToString
        @AllArgsConstructor
        public static class Reference implements Node.Expr {
            public final Token variable;
            public final Token identifier;

            @Override 
            public <T> T accept(NodeVisitor<T> visitor) { return visitor.visitReferenceExpr(this); }
        }

        @ToString
        @AllArgsConstructor
        public static class While implements Node.Expr {
            public final Node.Expr condition;
            public final Node.Stmt body;

            @Override 
            public <T> T accept(NodeVisitor<T> visitor) { return visitor.visitWhileExpr(this); }
        }

        @ToString
        @AllArgsConstructor
        public static class Until implements Node.Expr {
            public final Node.Expr condition;
            public final Node.Stmt body;

            @Override 
            public <T> T accept(NodeVisitor<T> visitor) { return visitor.visitUntilExpr(this); }
        }

        @ToString
        @AllArgsConstructor
        public static class If implements Node.Expr {
            public final Node.Expr condition;
            public final Node.Expr body;
            public final List<Couple<Node.Expr, Node.Expr>> elifs;
            public final Node.Expr elseBody;

            @Override
            public <T> T accept(NodeVisitor<T> visitor) { return visitor.visitIfExpr(this); }
        }

        @ToString
        @AllArgsConstructor
        public static class Block implements Node.Expr {
            public final Environment env;
            public final List<Node.Stmt> body;
        
            @Override 
            public <T> T accept(NodeVisitor<T> visitor) { return visitor.visitBlockExpr(this); }
        }
    }
}
