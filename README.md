<p align="center"><img width=12.5% src="https://i.blamejared.com/searchables.png"></p>
<p align="center"><img width=60% src="https://i.blamejared.com/searchables_banner.png"></p>

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
[![GitHub issues](https://img.shields.io/github/issues/jaredlll08/searchables?style=flat-square)](https://github.com/jaredlll08/searchables/issues)
[![GitHub license](https://img.shields.io/github/license/jaredlll08/searchables?color=0690ff&style=flat-square)](https://github.com/jaredlll08/searchables/blob/1.19.2/LICENSE)
[![Jenkins](https://img.shields.io/jenkins/build?jobUrl=https%3A%2F%2Fci.blamejared.com%2Fjob%2FJared%2Fjob%2FMinecraft%2520Mods%2Fjob%2FSearchables%2Fjob%2F1.19.2%2F&style=flat-square)](https://ci.blamejared.com/job/Jared/job/Minecraft%20Mods/job/Searchables/job/1.19.2/)
[![Discord](https://img.shields.io/badge/Discord-%237289DA?style=flat-square&logo=Discord&logoColor=white)](https://discord.blamejared.com/)
[![](http://cf.way2muchnoise.eu/searchables.svg?badge_style=flat)](https://minecraft.curseforge.com/projects/searchables)

## Table of Contents

- [Introduction](#introduction)
- [Code Examples](#code-examples)
- [Feedback](#feedback)
- [License](#license)
- [Setup](#setup)
- [Build](#build)
- [Maven](#maven)

## Introduction

Searchables is a library mod that adds helper methods that allow for searching and filtering elements based on components (`shape:square color:red`), as well as offering built in auto-complete functionality.

## Code Examples

Assuming you have the following code structure, which defines a `Shape` object and a list of Shapes:
```java
public record Shape(String name, String type, String colour) {}
    
public static final List<Shape> SHAPES = List.of(
        new Shape("one", "square", "red"),
        new Shape("two", "square", "dark blue"),
        new Shape("three", "circle", "red"),
        new Shape("four", "triangle", "yellow")
);
```

You may want to be able to filter that list of Shapes to find specific shapes or shapes that meet certain criteria.
To do this, you can define a `SearchableType<Shape>` object like so:

```java
public static final SearchableType<Shape> SHAPE = new SearchableType.Builder<Shape>() // Starts a builder
            .defaultComponent(SearchableComponent.create("name", shape -> Optional.of(shape.name()))) // Sets the name of the shape as the default component
            .component(SearchableComponent.create("type", shape -> Optional.of(shape.type()))) // Adds a new component for the type of the shape
            .component(SearchableComponent.create("color", shape -> Optional.of(shape.colour()))) // Adds a new component for the color of the shape
            .build();
```

Once you have a `SearchableType`, you are able to perform searches like so:
```java
SHAPE.filterEntries(SHAPES, "one"); // Returns a list with a single entry of Shape("one", "square", "red") - When searching for a default component, you don't need to specify the key
SHAPE.filterEntries(SHAPES, "color:red"); // Returns a list with the entries, Shape("one", "square", "red") and Shape("three", "circle", "red")
SHAPE.filterEntries(SHAPES, "one color:red"); // Returns a list with a single entry of Shape("one", "square", "red")
SHAPE.filterEntries(SHAPES, "color:red one"); // Returns a list with a single entry of Shape("one", "square", "red") - The search itself can be in any order
SHAPE.filterEntries(SHAPES, "color:'dark blue'"); // Returns a list with a single entry of Shape("two", "square", "dark blue") - terms with spaces, or quote characters need to be quoted - valid quote characters are '"`
```

Auto complete is done using a custom `EditBox` class, `AutoCompletingEditBox`, like so:
```java
public class MyScreen extends Screen {
    
    private AutoCompletingEditBox<TestConstants.Shape> search;
    
    protected MyScreen(Component title) {
        super(title);
    }
    
    @Override
    protected void init() {
        super.init();
        this.search = addRenderableWidget(new AutoCompletingEditBox<>(
                font, // The Font used for rendering text
                width / 2 - 75, // x
                22, // y
                Button.DEFAULT_WIDTH, // width
                Button.DEFAULT_HEIGHT, // height
                search, // This box - Used when init is re-run to carry over the old value
                SearchablesConstants.COMPONENT_SEARCH, // Message to display when the search is empty and unfocused
                TestConstants.SHAPE, // The SearchableType that will be searched
                () -> TestConstants.SHAPES)); // Supplier for the elements - used for auto complete
        this.search.addResponder(this::filter); // Adds a responder to the search to filter the elements on every change in the EditBox
        this.addRenderableOnly(this.search.autoComplete()); // Add the autocomplete widget as a renderable only
    }
    
    @Override
    public void tick() {
        super.tick();
        this.search.tick(); // Assists rendering the EditBox's cursor
    }
    
    @Override
    public boolean mouseScrolled(double xpos, double ypos, double delta) {
        // This is required to be able to scroll on the autocomplete widget to scroll
        if(search.autoComplete().mouseScrolled(xpos, ypos, delta)) {
            return true;
        }
        return super.mouseScrolled(xpos, ypos, delta);
    }

}
```

## Feedback

If you're looking for help with the mod, or just want to come hang out, we have a [Discord server](https://discord.blamejared.com).  
If you're running into a bug or have a feature request, please don't be afraid to make an [issue on the tracker](https://github.com/jaredlll08/searchables/issues).

## License

Distributed under the MIT License. See the [LICENSE](https://github.com/jaredlll08/searchables/blob/1.19.2/LICENSE) file for more information.

## Setup

To set up the Searchables development environment you must clone the repo.

```bash
git clone https://github.com/jaredlll08/searchables.git
```

After the project has been cloned and initialized you can directly import it into your IDE of choice.

## Build

Building the project is as easy as running a Gradle command!
Simply run:
```bash
gradlew build
```
and the outputted `.jar` files will be put in `build/libs/` folder of each subproject (`common/build/libs/`, `fabric/build/libs/` and `forge/build/libs/`).

## Maven

Every push to this repository is built and published to the [BlameJared](https://maven.blamejared.com) maven, to use these builds in your project, first add the BlameJared maven to your `repositories` block in your build.gradle file like so:

```groovy
repositories {
    maven { 
        url = 'https://maven.blamejared.com'
        name = 'BlameJared Maven'
    }
}
```

Then, depending on what modloader you are using, you can use the following snippets, just replace `[VERSION]` with the latest version for each artifact.

### Fabric [![Maven](https://img.shields.io/maven-metadata/v?color=C71A36&label=Latest%20version&logo=Latest%20version&metadataUrl=https%3A%2F%2Fmaven.blamejared.com%2Fcom%2Fblamejared%2Fsearchables%2FSearchables-fabric-1.19.2%2Fmaven-metadata.xml&style=flat-square)](https://maven.blamejared.com/com/blamejared/searchables/Searchables-fabric-1.19.2/)

```kotlin
dependencies {
    modImplementation("com.blamejared.searchables:Searchables-fabric-1.19.2:[VERSION]")
    // Example:
    // modImplementation("com.blamejared.searchables:Searchables-fabric-1.19.2:1.0.0")
}
```

### Forge [![Maven](https://img.shields.io/maven-metadata/v?color=C71A36&label=Latest%20version&logo=Latest%20version&metadataUrl=https%3A%2F%2Fmaven.blamejared.com%2Fcom%2Fblamejared%2Fsearchables%2FSearchables-forge-1.19.2%2Fmaven-metadata.xml&style=flat-square)](https://maven.blamejared.com/com/blamejared/searchables/Searchables-forge-1.19.2/)


```kotlin
dependencies {
    compileOnly(fg.deobf("com.blamejared.searchables:Searchables-forge-1.19.2:[VERSION]"))
    // Example:
    // implementation(fg.deobf("com.blamejared.searchables:Searchables-forge-1.19.2:1.0.0"))
}
```

### Common [![Maven](https://img.shields.io/maven-metadata/v?color=C71A36&label=Latest%20version&logo=Latest%20version&metadataUrl=https%3A%2F%2Fmaven.blamejared.com%2Fcom%2Fblamejared%2Fsearchables%2FSearchables-common-1.19.2%2Fmaven-metadata.xml&style=flat-square)](https://maven.blamejared.com/com/blamejared/searchables/Searchables-common-1.19.2/)

If you are in a multi-modloader environment (Such as [MultiLoader](https://github.com/jaredlll08/MultiLoader-Template)), you can bring the Common artifact (code that does not depend on any specific mod loader but rather just the vanilla game) into your Common project like so:

```kotlin
dependencies {
    compileOnly("com.blamejared.searchables:Searchables-common-1.19.2:[VERSION]")
    // Example:
    // compileOnly("com.blamejared.searchables:Searchables-common-1.19.2:1.0.0")
}
```
