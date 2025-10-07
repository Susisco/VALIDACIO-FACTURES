import { Anchor, Container, Stack, Text, Title } from "@mantine/core";

export default function PoliticaPrivadesa() {
  return (
    <Container size="md" py="xl">
      <Stack gap="lg">
        <Title order={1} ta="center" c="blue.7">
          Política de privadesa
        </Title>
        <Text>
          Aquesta política de privadesa descriu com l'aplicació de Validació de
          Factures recull, utilitza i protegeix les dades personals que
          proporcionen els usuaris. L'objectiu principal és garantir la
          transparència i protegir la informació que gestionem.
        </Text>

        <div>
          <Title order={2} size="h3">
            Responsable del tractament
          </Title>
          <Text>
            L'Ajuntament és el responsable del tractament de les dades i garanteix
            l'ús adequat d'acord amb la normativa vigent en matèria de protecció
            de dades.
          </Text>
        </div>

        <div>
          <Title order={2} size="h3">
            Finalitats del tractament
          </Title>
          <Text>
            Les dades personals s'utilitzen exclusivament per gestionar els
            accessos a l'aplicació i permetre la validació i el seguiment de
            factures, albarans, pressupostos i altres tràmits relacionats.
          </Text>
        </div>

        <div>
          <Title order={2} size="h3">
            Base legal
          </Title>
          <Text>
            El tractament es basa en el compliment d'una missió realitzada en
            interès públic i en l'exercici dels poders públics conferits a
            l'Ajuntament.
          </Text>
        </div>

        <div>
          <Title order={2} size="h3">
            Conservació de les dades
          </Title>
          <Text>
            Les dades es conservaran durant el temps necessari per complir la
            finalitat amb què van ser recollides i d'acord amb els terminis de
            conservació previstos per la normativa aplicable.
          </Text>
        </div>

        <div>
          <Title order={2} size="h3">
            Destinataris
          </Title>
          <Text>
            No es realitzaran cessió de dades a tercers, excepte en els casos
            previstos legalment o quan sigui imprescindible per prestar el
            servei.
          </Text>
        </div>

        <div>
          <Title order={2} size="h3">
            Drets de les persones interessades
          </Title>
          <Text>
            Els usuaris poden exercir els drets d'accés, rectificació, supressió,
            oposició, limitació del tractament i portabilitat de les seves dades.
            Per fer-ho, poden dirigir-se al correu electrònic de contacte o a
            l'Oficina d'Atenció Ciutadana.
          </Text>
        </div>

        <div>
          <Title order={2} size="h3">
            Mesures de seguretat
          </Title>
          <Text>
            Implementem mesures tècniques i organitzatives per garantir la
            confidencialitat, integritat i disponibilitat de les dades personals.
          </Text>
        </div>

        <div>
          <Title order={2} size="h3">
            Contacte
          </Title>
          <Text>
            Per a qualsevol consulta relacionada amb la protecció de dades es pot
            contactar amb l'Ajuntament a través del correu electrònic
            <Anchor href="mailto:ajuntament@example.org">ajuntament@example.org</Anchor>
            o presencialment a l'Oficina d'Atenció Ciutadana.
          </Text>
        </div>

        <Text size="sm" c="dimmed" ta="center">
          Darrera actualització: gener de 2024.
        </Text>
      </Stack>
    </Container>
  );
}
