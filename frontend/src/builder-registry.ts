// src/builder-registry.ts
import { Builder } from '@builder.io/react';
import {
  Button,
  Card,
  Grid,
  Text,
  Container,
  // …añade aquí todos los componentes de Mantine que necesites
} from '@mantine/core';

Builder.registerComponent(Grid, {
  name: 'Mantine Grid',
  // a Grid le bastará con ser contenedor de columnas
});

Builder.registerComponent(Button, {
  name: 'Mantine Button',
  inputs: [
    { name: 'children', type: 'text', defaultValue: 'Haz clic' },
    { name: 'variant', type: 'text', defaultValue: 'filled' },
  ],
});

Builder.registerComponent(Card, {
  name: 'Mantine Card',
  inputs: [
    { name: 'children', type: 'richText' },
    { name: 'shadow', type: 'text', defaultValue: 'sm' },
  ],
});

Builder.registerComponent(Text, {
  name: 'Mantine Text',
  inputs: [{ name: 'children', type: 'richText', defaultValue: 'Texto...' }],
});

Builder.registerComponent(Container, {
  name: 'Mantine Container',
  inputs: [{ name: 'size', type: 'text', defaultValue: 'md' }],
});

// …y así sucesivamente para otros componentes que quieras arrastrar.
