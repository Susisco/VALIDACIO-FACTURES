import React from "react";
import {
  Modal,
  Text,
  Stack,
  Divider,
  ScrollArea,
} from "@mantine/core";
import type { HistoricCanvi } from "../api/historic";

interface Props {
  opened: boolean;
  onClose: () => void;
  canvis: HistoricCanvi[];
}

export default function HistoricCanvisModal({
  opened,
  onClose,
  canvis,
}: Props) {
  return (
    <Modal
      opened={opened}
      onClose={onClose}
      title="Històric de canvis"
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
    >
       {canvis.length === 0 ? (
        <Text size="sm" c="dimmed">
          No s'han registrat canvis per aquest albarà.
        </Text>
      ) : (
        <ScrollArea style={{ maxHeight: 400 }} type="auto">
          <Stack gap="sm"> {/* ✅ Mantine v7 usa `gap` en lloc de `spacing` */}
            {canvis.map((canvi, index) => (
              <div key={index}>
                <Text size="sm">
                  <strong>{canvi.usuari?.nom || "Usuari desconegut"}</strong> –{" "}
                  {new Date(canvi.dataHora).toLocaleString("ca-ES")}
                </Text>

                {index < canvis.length - 1 && <Divider />}
              </div>
            ))}
          </Stack>
        </ScrollArea>
      )}
    </Modal>
    );
  }