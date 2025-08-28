import React, { useState } from "react";
import { Modal, ScrollArea, Box, Text, TextInput } from "@mantine/core";
import type { Proveidor } from "../api/proveidors";

interface Props {
  opened: boolean;
  proveidors: Proveidor[];
  onSelect: (p: Proveidor) => void;
  onClose: () => void;
}

export default function ModalProveidorSelector({
  opened,
  proveidors,
  onSelect,
  onClose,
}: Props) {
  const [searchName, setSearchName] = useState("");
  const [searchNIF, setSearchNIF] = useState("");

  // Filtrar proveedores por nombre y NIF
  const filteredProveidors = proveidors.filter(
    (p) =>
      p.nomComercial.toLowerCase().includes(searchName.toLowerCase()) &&
      p.nif.toLowerCase().includes(searchNIF.toLowerCase())
  );

  return (
    <Modal
      opened={opened}
      onClose={onClose}
      title="Selecciona un proveïdor"
      centered
      withinPortal={false}
      withCloseButton={false}
      overlayProps={{
        opacity: 1,
        color: "#ffffff",
        style: {
          position: "fixed",
          top: 0,
          left: 0,
          width: "100%",
          height: "100%",
        },
      }}
      styles={{
        close: { fontSize: 30 },
      }}
    >
      {/* Campos de búsqueda */}
      <Box mb="md">
        <TextInput
          placeholder="Buscar por nombre"
          value={searchName}
          onChange={(e) => setSearchName(e.currentTarget.value)}
          mb="sm"
        />
        <TextInput
          placeholder="Buscar por NIF"
          value={searchNIF}
          onChange={(e) => setSearchNIF(e.currentTarget.value)}
        />
      </Box>

      {/* Lista de proveedores filtrados */}
<ScrollArea style={{ height: 250 }}>
  {filteredProveidors.map((p) => (
    <Box
      key={p.id}
      p="sm"
      style={{
        display: "flex",
        flexDirection: "row", // Mantiene la disposición horizontal
        justifyContent: "space-between", // Distribuye los elementos horizontalmente
        alignItems: "center", // Centra los elementos verticalmente
        borderRadius: 6,
        border: "1px solid #ddd",
        cursor: "pointer",
        marginBottom: 8,
        width: "100%", // Ocupa todo el ancho disponible
        
        transition: "background 0.2s ease-in-out",
      }}
      onMouseEnter={(e) => (e.currentTarget.style.background = "#f8f9fa")}
      onMouseLeave={(e) =>
        (e.currentTarget.style.background = "transparent")
      }
      onClick={() => {
        onSelect(p);
        onClose();
      }}
    >
      <Text fw={500} style={{ flex: 1, marginRight: "8px" }}>
        {p.nomComercial}
      </Text>
      <Text size="xs" c="gray" style={{ flex: 1, textAlign: "left" }}>
        {p.nif}
      </Text>
    </Box>
  ))}
</ScrollArea>
    </Modal>
  );
}