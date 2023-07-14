JAVAC = javac
JAVAFLAGS = -d bin

SRCDIR = src
BINDIR = bin

SOURCES := $(wildcard $(SRCDIR)/*.java)
CLASSES := $(SOURCES:$(SRCDIR)/%.java=$(BINDIR)/%.class)

all: $(CLASSES)

$(BINDIR)/%.class: $(SRCDIR)/%.java
	@mkdir -p $(BINDIR)
	$(JAVAC) $(JAVAFLAGS) $<

clean:
	@rm -rf $(BINDIR)
